public class GoodClass {

static byte[] rol(byte[] num)
        {
            byte[] result = new byte[16];

            string binary = "";
            string res = "";
            binary = toBinary(num);
            binary = binary;
            res = binary.Remove(0, 1);
            res += binary[0];
            result = fromBinary(res);

            return result;
        }
        
static byte[] EVGENY(byte[] num)
        {
            byte[] result = new byte[16];

            string binary = "";
            string res = "";
            binary = toBinary(num);
            binary = binary;
            res = binary.Remove(0, 1);
            res += binary[0];
            result = fromBinary(res);

            return result;
        }        
        
}