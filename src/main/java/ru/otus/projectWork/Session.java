package ru.otus.projectWork;

import java.nio.file.Path;

/**
 * @author Sergei on 12.08.2024 17:16.
 * @project msDosCommand
 */
public class Session {

    private Path currentPath;

    public Path getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(Path currentPath) {
        this.currentPath = currentPath;
    }

    public Session(Path currentPath) {
        this.currentPath = currentPath;
    }
}
