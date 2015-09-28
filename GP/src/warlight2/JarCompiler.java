package warlight2;

import java.io.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Created by Jonatan on 21-Sep-15.
 */
public class JarCompiler {

    public void run(String source_dir, String target_path) throws IOException
    {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().putValue("Main-Class", "bot.BotStarter");
        JarOutputStream target = new JarOutputStream(new FileOutputStream(target_path), manifest);
        add(new File(source_dir + "//bot"), target, "myBOt//out//production//myBot");
        add(new File(source_dir + "//move"), target, "myBOt//out//production//myBot");
        add(new File(source_dir + "//map"), target, "myBOt//out//production//myBot");
        target.close();
    }

    public static void add(File source, JarOutputStream target, String removeme)
            throws IOException
    {
        BufferedInputStream in = null;
        try
        {
            File parentDir = new File(removeme);
            File source2 = new File(source.getCanonicalPath().substring(
                    parentDir.getCanonicalPath().length() + 1,
                    source.getCanonicalPath().length()));
            if (source.isDirectory())
            {
                String name = source2.getPath().replace("\\", "/");
                if (!name.isEmpty())
                {
                    if (!name.endsWith("/"))
                        name += "/";
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile : source.listFiles())
                    add(nestedFile, target, removeme);
                return;
            }

            JarEntry entry = new JarEntry(source2.getPath().replace("\\", "/"));
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));

            byte[] buffer = new byte[2048];
            while (true)
            {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        }
        finally
        {
            if (in != null)
                in.close();
        }
    }
}
