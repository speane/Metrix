using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace McCabe
{
    public class ViewModel
    {
        public int MainCC { get; set; }
        public int MainMeyers { get; set; }
    } 
   
    // класс с полями для php функции
    public class Function
    {
        public string Name { get; set; }
        public string Inner { get; set; } // поле для хранения внутренностей функции 
        public int CC { get; set; }
        public int MeyersCC { get; set; }
    }

    class Source
    {
        public string Inner { get; set; } // поле для хранения полного исходника
        public int CyclomaticComplexity { get; set; }
        public int MeyersCyclomaticComplexity { get; set; }


        public void Source(string src)
        {
            CyclomaticComplexity = MeyersCyclomaticComplexity;
            src = DeleteText(src);
            src = DeleteComments(src);
            if (!isCorrect(src))
            {
                throw new IOException("Некорректный исходный код");
            }
            Inner = src; // записывает исходник 
            Functions = FillFunctions(src); // заполняет список функций
        }


        // проверка на фигурные скобки и пустую строку
        private bool isCorrect(string src)
        {
            bool res = true;
            Regex begin = new Regex("{");
            Regex end = new Regex("}");
            Regex space = new Regex(" ");
            src = space.Replace(src, "");
            if (src == "")
            {
                res = false;
            }
            if (begin.Matches(src).Count != end.Matches(src).Count)
            {
                res = false;
            }
            return res;
            CyclomaticComplexity = MeyersCyclomaticComplexity;
        }

        // удаляет текст в кавычках
        private string DeleteText(string src)
        {
            Regex textPattern = new Regex(@"""\s*(.*?)\s*\""", RegexOptions.IgnoreCase);
            string res = textPattern.Replace(src, "");
            return textPattern.Replace(src, "");
        }

        // удаляет комментарии
        private string DeleteComments(string src)
        {
            Regex textPattern = new Regex(@"(/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+/)|(//.*)", RegexOptions.IgnoreCase);
            string res = textPattern.Replace(src, "");
            Regex endPattern = new Regex(@"\?>", RegexOptions.IgnoreCase);
            res = endPattern.Replace(res, "");
            Regex beginPattern = new Regex(@"<\?", RegexOptions.IgnoreCase);
            res = beginPattern.Replace(res, "");


            return res;
        }

        // ищет позицию закрывающей фигурной скобки для первой открывающей
        private int EndBrakePosition(string src)
        {
            int counter = 0;
            int result = 0;
            for (int i = 0; (i < src.Length); i++)
            {
                if (src[i] == '{')
                {
                    counter++;
                }
                if (src[i] == '}')
                {
                    counter--;
                    if (counter == 0)
                    {
                        result = i;
                        break;
                    }
                }
            }
            return result;
        }

        // возвращает текст в первых попавшихся фигурных скобках
        private string NextTextInBrakes(string src)
        {
            string result = src.Remove(0, src.IndexOf('{') + 1).Remove(1 + EndBrakePosition(src.Remove(0, src.IndexOf('{'))));

            return result;
        }

        // удаляет текст в следующих фигурных скобках
        private string DeleteNextTextInBrakes(string src)
        {
            return src.Remove(0, EndBrakePosition(src));
        }


        // возвращает цикломатическое число
        private int setCC(string src)
        {
            int CyclomaticComplexity = 1;

            Regex ifPattern = new Regex("if", RegexOptions.IgnoreCase);
            int a = ifPattern.Matches(src).Count;
            CyclomaticComplexity += ifPattern.Matches(src).Count;


            CyclomaticComplexity += src.Where(x => x == '?').Count();

            Regex casePattern = new Regex("case", RegexOptions.IgnoreCase);
            CyclomaticComplexity += casePattern.Matches(src).Count;

            Regex forPattern = new Regex("for", RegexOptions.IgnoreCase);
            CyclomaticComplexity += forPattern.Matches(src).Count;

            Regex whilePattern = new Regex("while", RegexOptions.IgnoreCase);
            CyclomaticComplexity += whilePattern.Matches(src).Count;

            return CyclomaticComplexity;
        }

        // возвращает значение метрики Майерса
        private int MeyersCC(string src)
        {
            int CyclomaticComplexity = 1;
            Regex Predicates = new Regex(@"( and)|( or)|(&&)|(\|\|)");

            List<int> indexes = new List<int>(); // индексы слов function

            CyclomaticComplexity += src.Where(x => x == '?').Count();
            Regex casePattern = new Regex("case", RegexOptions.IgnoreCase);
            CyclomaticComplexity += casePattern.Matches(src).Count;

            Regex ifPattern = new Regex("if", RegexOptions.IgnoreCase);
            for (int i = 0; i < ifPattern.Matches(src).Count; i++)
            {
                indexes.Add(ifPattern.Matches(src)[i].Index);
            }
            foreach (var i in indexes)
            {
                CyclomaticComplexity += 1 + Predicates.Matches(NextTextInParenthesis(src.Remove(0, i))).Count;
            }
            indexes.Clear();

            Regex forPattern = new Regex("for", RegexOptions.IgnoreCase);
            for (int i = 0; i < forPattern.Matches(src).Count; i++)
            {
                indexes.Add(forPattern.Matches(src)[i].Index);
            }
            foreach (var i in indexes)
            {
                CyclomaticComplexity += 1 + Predicates.Matches(NextTextInParenthesis(src.Remove(0, i))).Count;
            }
            indexes.Clear();

            Regex whilePattern = new Regex("while", RegexOptions.IgnoreCase);
            for (int i = 0; i < whilePattern.Matches(src).Count; i++)
            {
                indexes.Add(whilePattern.Matches(src)[i].Index);
            }
            foreach (var i in indexes)
            {
                CyclomaticComplexity += 1 + Predicates.Matches(NextTextInParenthesis(src.Remove(0, i))).Count;
            }
            indexes.Clear();

            return CyclomaticComplexity;
        }

        //  возвращает модель представления
        public ViewModel Information()
        {
            ViewModel result = new ViewModel();
            int MainCC = 0;
            int MainMeyersCC = 0;
            result.Functions = Functions;

            int commonCC = Functions.Count() + setCC(Inner);
            int funcCC = 0;
            foreach (var f in Functions)
            {
                funcCC += f.CC;
            }
            if ((commonCC - funcCC) > 1)
            {
                MainCC = commonCC - funcCC;
            }
            else
            {
                MainCC = 0;
            }

            int commonMeyersCC = Functions.Count() + MeyersCC(Inner);
            int funcMeyersCC = 0;
            foreach (var f in Functions)
            {
                funcMeyersCC += f.MeyersCC;
            }
            if ((commonMeyersCC - funcMeyersCC) > 1)
            {
                MainMeyersCC = commonMeyersCC - funcMeyersCC;
            }
            else
            {
                MainMeyersCC = 0;
            }

            result.MainCC = MainCC;
            result.MainMeyers = MainMeyersCC;

            return result;
        }


        // ищет позицию закрывающей круглой скобки для первой открывающей
        private int EndParenthesisPosition(string src)
        {
            int counter = 0;
            int result = 0;
            for (int i = 0; (i < src.Length); i++)
            {
                if (src[i] == '(')
                {
                    counter++;
                }
                if (src[i] == ')')
                {
                    counter--;
                    if (counter == 0)
                    {
                        result = i;
                        break;
                    }
                }
            }
            return result;
        }

        // возвращает текст в первых попавшихся круглых скобках
        private string NextTextInParenthesis(string src)
        {
            string result = src.Remove(0, src.IndexOf('(') + 1).Remove(1 + EndParenthesisPosition(src.Remove(0, src.IndexOf('('))));

            return result;
        }

        // удаляет текст в следующих круглых скобках
        private string DeleteNextTextInParenthesis(string src)
        {
            return src.Remove(0, EndParenthesisPosition(src));
        }



    }
}