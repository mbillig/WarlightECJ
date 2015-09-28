package warlight2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Created by Jonatan on 24-Sep-15.
 */
public class MyBotCompiler {


    public void run(String target_path) throws IOException
    {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().putValue("Main-Class", "bot.BotStarter");
        JarOutputStream target = new JarOutputStream(new FileOutputStream(target_path), manifest);
        addToJar("myBot\\out\\production\\myBot", target);
        target.close();
    }

    public void addToJar(String path, JarOutputStream target){


        File parentDir = new File(path);
        File source = new File(path);
        try {
            String relPath = source.getCanonicalPath()
                    .substring(parentDir.getCanonicalPath().length() + 1,
                            source.getCanonicalPath().length());

            File bot = new File(path + "\\bot");
            File move = new File(path + "\\move");
            File map = new File(path + "\\map");
            for(File nestedFile: bot.listFiles()){

                JarEntry entry = new JarEntry(relPath.replace("\\", "/"));
            }
        } catch (Exception e){
            System.out.println("pathing failure");
        }
    }
}
