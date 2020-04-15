package org.shan;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class SimpleFileType extends LanguageFileType {

    public static final SimpleFileType INSTANCE = new SimpleFileType();

    private SimpleFileType() {
        super(SimpleLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return Constants.FILE_TYPE_NAME;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Neat Form JSON File";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return Constants.FILE_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SimpleIcons.ICON;
    }
}
