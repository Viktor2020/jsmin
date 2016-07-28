package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Display extends JFrame implements Handler{
    private JList<String> log;
    private JButton btn;
    private JTextField from;
    private JPanel root;
    private JTextField to;
    private JProgressBar progress;
    private DefaultListModel<String> model;
    private MyFileFindVisitor visitor;

    Path startPath = Paths.get("www/planer");
    Path newPath = Paths.get("wwwmin/planer1");

    //Строка с glob-шаблоном
    String pattern = "glob:*.js";

    //Строка с regex-шаблоном
    //String pattern = "regex:\\S+\\.java";

    public Display() throws HeadlessException {
        super("Compressor");
        setContentPane(root);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        model = new DefaultListModel<>();
        log.setModel(model);
        visitor = new MyFileFindVisitor(pattern, startPath, newPath);
        visitor.addHandler (this);


        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(from.getText());
                if (from.getText().length() > 0) {
                    startPath = Paths.get(from.getText());
                } else {
                    startPath = Paths.get("www");
                    from.setText("www");
                }
                if (to.getText().length() > 0) {
                    newPath = Paths.get(to.getText());
                } else {
                    newPath = Paths.get("wwwmin");
                    to.setText("wwwmin");
                }

                Run run = new Run();
                Thread thread = new Thread(run);
                thread.start();
            }
        });
    }

    class Run implements Runnable {

        @Override
        public void run() {
            try {
                if (newPath.toAbsolutePath().equals(startPath.toAbsolutePath())) return;

                progress.setIndeterminate(true);
                visitor.setFinishPath(newPath);
                visitor.setStartPath(startPath);
                Files.walkFileTree(startPath, visitor);
                System.out.println("complete");
            } catch (IOException e) {
                e.printStackTrace();
            }
            progress.setIndeterminate(false);
        }
    }

    @Override
    public String handle(String string) {
        model.insertElementAt(string, 0);
        return null;
    }
}
