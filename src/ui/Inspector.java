package ui;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MyFileFindVisitor extends SimpleFileVisitor<Path> {

    private Path startPath;
    private Path finishPath;
    private JSMin jsmin = new JSMin();
    private List<Handler> handlers;

    private PathMatcher matcher;



    public MyFileFindVisitor(String pattern, Path startPath, Path finishPath) {
        handlers = new ArrayList<>();
        this.startPath = startPath;
        this.finishPath = finishPath;
//        try {
            matcher = FileSystems.getDefault().getPathMatcher(pattern);
//        } catch (IllegalArgumentException iae) {
//            System.err.println("Invalid pattern; did you forget to prefix \"glob:\" or \"regex:\"?");
//            System.exit(1);
//        }

    }

    public void addHandler (Handler handler) {
        handlers.add(handler);
    }

    public void removeHandler(Handler handler) {
        handlers.remove(handler);
    }

    public Path getFinishPath() {
        return finishPath;
    }

    public void setFinishPath(Path finishPath) {
        this.finishPath = finishPath;
    }

    public Path getStartPath() {
        return startPath;
    }

    public void setStartPath(Path startPath) {
        this.startPath = startPath;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes fileAttributes) {
        find(path);
        return FileVisitResult.CONTINUE;
    }

    private void find(Path path) {
        Path name = path.getFileName();
        for (Handler handler : handlers) {
            handler.handle(path.toString());
        }
        System.out.println(name);
        String currentPath = path.toAbsolutePath().toString();
        String startPath = this.startPath.toAbsolutePath().toString();
        String finishPath = this.finishPath.toAbsolutePath().toString();
        String newFileName = currentPath.replace(startPath, finishPath);
        File to  = new File(newFileName.replace(name.toString(), ""));
        to.mkdirs();
        isCmpress = true;
        String [] ss = name.toString().split("\\.");
        for (String s : ss) {
            if (s.equals("min")) {
                isCmpress = false;
            }
        }

        if (isCmpress && matcher.matches(name)) {

            try {


                jsmin.setIn(new FileInputStream(path.toFile()));
                jsmin.setOut(new FileOutputStream(Paths.get(newFileName).toFile()));
                jsmin.jsmin();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JSMin.UnterminatedCommentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSMin.UnterminatedStringLiteralException e) {
                e.printStackTrace();
            } catch (JSMin.UnterminatedRegExpLiteralException e) {
                e.printStackTrace();
            }
//            System.out.println("Matching file:" + path.getFileName());


        } else {
//            System.out.println("!!--!!--!!--!!\n!!--!!--!!");
           try {
                Files.copy(path, Paths.get(newFileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean isCmpress = false;
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes fileAttributes) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return super.postVisitDirectory(dir, exc);
    }
}

public class Inspector {
    public static void main(String[] args) {
        Path startPath = Paths.get("www/planer");
        Path newPath = Paths.get("wwwmin/planer1");

        //Строка с glob-шаблоном
        String pattern = "glob:*.js";

        //Строка с regex-шаблоном
        //String pattern = "regex:\\S+\\.java";
        try {
            FileVisitor<Path> visitor = new MyFileFindVisitor(pattern, startPath, newPath);
            Files.walkFileTree(startPath, visitor);
            System.out.println("File search completed!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}