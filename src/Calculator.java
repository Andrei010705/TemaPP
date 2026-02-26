import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class Calculator extends JFrame {
    JTextArea textArea = new JTextArea(3, 5);
    JButton numere[] = {
            new JButton("0"),
            new JButton("1"),
            new JButton("2"),
            new JButton("3"),
            new JButton("4"),
            new JButton("5"),
            new JButton("6"),
            new JButton("7"),
            new JButton("8"),
            new JButton("9")
    };
    JButton operatori[] = {
            new JButton("+"),
            new JButton("-"),
            new JButton("*"),
            new JButton("/"),
            new JButton("="),
            new JButton("C")
    };

    public Calculator() {
        add(new JScrollPane(textArea), BorderLayout.NORTH);

        JPanel panel = new JPanel(new FlowLayout());

        for (JButton btn : numere) panel.add(btn);
        for (JButton btn : operatori) panel.add(btn);


        panel.add(new JButton("("));
        panel.add(new JButton(")"));


        add(panel, BorderLayout.CENTER);

        textArea.setEditable(false);

        for (int i=0;i<numere.length;i++) {
            int n = i;
            numere[i].addActionListener(e -> textArea.append(Integer.toString(n)));
        }

        for (int i = 0; i< operatori.length; i++) {
            int opIndex = i;
            operatori[i].addActionListener(e -> {
                if (opIndex == 5) { // C
                    textArea.setText("");
                } else if (opIndex == 4) { // =
                    try {
                        String expr = textArea.getText();
                        String rpn = infixToRPN(expr);
                        double result = evaluateRPN(rpn);
                        textArea.setText(expr + " = " + result + " " + rpn);
                    } catch (Exception ex) {
                        textArea.setText("Eroare");
                    }
                } else {
                    textArea.append(" " + operatori[opIndex].getText() + " ");
                }
            });
        }
    }

    // Infix → RPN

    public static void main(String[] args) {
        Calculator c = new Calculator();
        c.setSize(300,200);
        c.setVisible(true);
        c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}