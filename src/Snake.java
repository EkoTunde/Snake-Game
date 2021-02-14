import java.awt.*;
import javax.swing.*;

public class Snake extends JFrame {

    public Snake() {
        initUI();
    }

    private void initUI() {

        Game game = new Game();
        add(game);
        setResizable(false);
        pack();

        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame ex = new Snake();
            ex.setVisible(true);
        });
    }
}