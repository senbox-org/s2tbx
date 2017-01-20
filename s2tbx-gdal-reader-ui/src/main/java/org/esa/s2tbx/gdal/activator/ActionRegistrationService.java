package org.esa.s2tbx.gdal.activator;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.Arrays;

import javax.swing.Action;

@ServiceProvider(service= ActionRegistrationService.class)
public class ActionRegistrationService {

    public ActionRegistrationService() {
    }

    public void registerAction(String category, String menuPath, Action action) throws IOException {
        String actionName = (String)action.getValue(Action.NAME);
        // add update action
        String originalFile = "Actions/" + category + "/" + actionName + ".instance";
        FileObject in = getFolderAt("Actions/" + category);
        FileObject obj = in.getFileObject(actionName, "instance");
        if (obj == null) {
            obj = in.createData(actionName, "instance");
        }
        obj.setAttribute("instanceCreate", action);
        obj.setAttribute("instanceClass", action.getClass().getName());

        // add update menu
        in = getFolderAt(menuPath);
        obj = in.getFileObject(actionName, "shadow");
        // create if missing
        if (obj == null) {
            obj = in.createData(actionName, "shadow");
            obj.setAttribute("originalFile", originalFile);
        }
    }

    private FileObject getFolderAt(String inputPath) throws IOException {
        String parts[] = inputPath.split("/");
        FileObject existing = FileUtil.getConfigFile(inputPath);
        if (existing != null)
            return existing;

        FileObject base = FileUtil.getConfigFile(parts[0]);
        if (base == null) {
            return null;
        }
        for (int i = 1; i < parts.length; i++) {
            String path = joinPath("/", Arrays.copyOfRange(parts, 0, i+1));
            FileObject next = FileUtil.getConfigFile(path);
            if (next == null) {
                next = base.createFolder(parts[i]);
            }
            base = next;
        }
        return FileUtil.getConfigFile(inputPath);
    }

    private static String joinPath(String separator, String parts[]) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                str.append(separator);
            }
            str.append(parts[i].toString());
        }
        return str.toString();
    }
}
