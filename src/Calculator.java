import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Calculator extends JFrame {
    private final JTextArea textArea = new JTextArea(3, 20);

    private final JButton[] numere = {
            new JButton("0"), new JButton("1"), new JButton("2"),
            new JButton("3"), new JButton("4"), new JButton("5"),
            new JButton("6"), new JButton("7"), new JButton("8"),
            new JButton("9")
    };

    private final JButton[] operatori = {
            new JButton("+"),
            new JButton("-"),
            new JButton("*"),
            new JButton("/"),
            new JButton("="),
            new JButton("C")
    };

    private final JButton parantezaStanga = new JButton("(");
    private final JButton parantezaDreapta = new JButton(")");

    public Calculator() {
        super("Calculator");

        setLayout(new BorderLayout());

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        add(new JScrollPane(textArea), BorderLayout.NORTH);

        JPanel panel = new JPanel(new FlowLayout());

        for (JButton btn : numere) {
            panel.add(btn);
        }

        for (JButton btn : operatori) {
            panel.add(btn);
        }

        panel.add(parantezaStanga);
        panel.add(parantezaDreapta);

        add(panel, BorderLayout.CENTER);

        // Butoane cifre
        for (int i = 0; i < numere.length; i++) {
            int cifra = i;
            numere[i].addActionListener(e -> textArea.append(String.valueOf(cifra)));
        }

        // Butoane operatori
        for (JButton operator : operatori) {
            operator.addActionListener(e -> {
                String text = operator.getText();

                if (text.equals("C")) {
                    textArea.setText("");
                } else if (text.equals("=")) {
                    calculeaza();
                } else {
                    textArea.append(" " + text + " ");
                }
            });
        }

        // Paranteze
        parantezaStanga.addActionListener(e -> textArea.append(" ( "));
        parantezaDreapta.addActionListener(e -> textArea.append(" ) "));
    }

    private void calculeaza() {
        try {
            String expr = textArea.getText().trim();

            if (expr.isEmpty()) {
                return;
            }

            List<String> rpn = infixToRPN(expr);
            double rezultat = evaluateRPN(rpn);

            textArea.setText(String.valueOf(rezultat));
        } catch (Exception ex) {
            textArea.setText("Eroare");
        }
    }

    // Transformă expresia infixată în RPN
    private List<String> infixToRPN(String expr) {
        Stack<Character> stiva = new Stack<>();
        List<String> rezultat = new ArrayList<>();
        StringBuilder numar = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                numar.append(c);
            } else if (c == '-' && esteMinusUnar(expr, i)) {
                numar.append(c);
            } else {
                if (numar.length() > 0) {
                    rezultat.add(numar.toString());
                    numar.setLength(0);
                }

                if (c == '(') {
                    stiva.push(c);
                } else if (c == ')') {
                    while (!stiva.isEmpty() && stiva.peek() != '(') {
                        rezultat.add(String.valueOf(stiva.pop()));
                    }

                    if (stiva.isEmpty()) {
                        throw new IllegalArgumentException("Paranteze incorecte");
                    }

                    stiva.pop(); // scoate '('
                } else if (esteOperator(c)) {
                    while (!stiva.isEmpty()
                            && stiva.peek() != '('
                            && precedence(stiva.peek()) >= precedence(c)) {
                        rezultat.add(String.valueOf(stiva.pop()));
                    }
                    stiva.push(c);
                } else {
                    throw new IllegalArgumentException("Caracter invalid: " + c);
                }
            }
        }

        if (numar.length() > 0) {
            rezultat.add(numar.toString());
        }

        while (!stiva.isEmpty()) {
            if (stiva.peek() == '(' || stiva.peek() == ')') {
                throw new IllegalArgumentException("Paranteze incorecte");
            }
            rezultat.add(String.valueOf(stiva.pop()));
        }

        return rezultat;
    }

    private double evaluateRPN(List<String> expr) {
        Stack<Double> s = new Stack<>();

        for (String element : expr) {
            if (esteNumar(element)) {
                s.push(Double.parseDouble(element));
            } else if (element.length() == 1 && esteOperator(element.charAt(0))) {
                if (s.size() < 2) {
                    throw new IllegalArgumentException("Expresie invalida");
                }

                double b = s.pop();
                double a = s.pop();

                switch (element.charAt(0)) {
                    case '+':
                        s.push(a + b);
                        break;
                    case '-':
                        s.push(a - b);
                        break;
                    case '*':
                        s.push(a * b);
                        break;
                    case '/':
                        s.push(a / b);
                        break;
                    default:
                        throw new IllegalArgumentException("Operator invalid");
                }
            } else {
                throw new IllegalArgumentException("Token invalid: " + element);
            }
        }

        if (s.size() != 1) {
            throw new IllegalArgumentException("Expresie invalida");
        }

        return s.pop();
    }

    private boolean esteNumar(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        int i = 0;
        boolean areCifra = false;
        boolean arePunct = false;

        if (text.charAt(0) == '-') {
            if (text.length() == 1) {
                return false;
            }
            i = 1;
        }

        for (; i < text.length(); i++) {
            char c = text.charAt(i);

            if (Character.isDigit(c)) {
                areCifra = true;
            } else if (c == '.') {
                if (arePunct) {
                    return false;
                }
                arePunct = true;
            } else {
                return false;
            }
        }

        return areCifra;
    }

    private boolean esteOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int precedence(char op) {
        if (op == '*' || op == '/') {
            return 2;
        }
        if (op == '+' || op == '-') {
            return 1;
        }
        return 0;
    }

    private boolean esteMinusUnar(String expr, int pozitie) {
        if (expr.charAt(pozitie) != '-') {
            return false;
        }

        int i = pozitie - 1;
        while (i >= 0 && Character.isWhitespace(expr.charAt(i))) {
            i--;
        }

        if (i < 0) {
            return true; // la începutul expresiei
        }

        char anterior = expr.charAt(i);
        return anterior == '(' || esteOperator(anterior);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculator c = new Calculator();
            c.setSize(350, 220);
            c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            c.setVisible(true);
        });
    }
}