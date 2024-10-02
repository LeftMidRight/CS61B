/** Class that prints the Collatz sequence starting from a given number.
 *  @author LeftMidRight
 */
public class Collatz {

    /** if n is even return n / 2
     *  else if n is odd return n * 3 + 1
     */
    public static int nextNumber(int n) {
        if(n % 2 == 1) {
            return n * 3 + 1;
        }
        return n / 2;
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
        System.out.println();
    }
}

