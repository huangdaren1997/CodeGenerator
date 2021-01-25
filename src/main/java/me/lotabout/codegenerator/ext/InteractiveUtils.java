package me.lotabout.codegenerator.ext;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.generation.GenerateMembersUtil;
import com.intellij.codeInsight.generation.GenerationInfo;
import com.intellij.codeInsight.generation.PsiElementClassMember;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.codeInsight.generation.PsiMethodMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.ide.util.TreeFileChooser;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiUtil;
/**
 * Utility for interact with Idea
 *
 * @author hdr
 */
public class InteractiveUtils {

    private static final Logger logger = Logger.getInstance(InteractiveUtils.class);

    public static PsiClass selectClass(@Nullable String dialogTitle, Project project) {
        if (dialogTitle == null) {
            dialogTitle = "select a class";
        }

        TreeClassChooserFactory chooserFactory = ServiceManager.getService(project, TreeClassChooserFactory.class);
        TreeClassChooser chooser = chooserFactory.createAllProjectScopeChooser(dialogTitle);
        chooser.showDialog();
        return chooser.getSelected();
    }

    public static PsiFile selectFile(@Nullable String dialogTitle) {
        if (dialogTitle == null) {
            dialogTitle = "select a file";
        }

        TreeClassChooserFactory chooserFactory = ServiceManager.getService(TreeClassChooserFactory.class);
        TreeFileChooser chooser = chooserFactory.createFileChooser(dialogTitle, null, null, null);
        chooser.showDialog();
        return chooser.getSelectedFile();
    }

    private static List<PsiMember> selectMembers() {
        // todo
        return Lists.newArrayList();
    }

    public static List<PsiField> selectFields(@NotNull PsiClass psiClass,
        @Nullable String dialogTitle,
        boolean allowEmptySelection,
        boolean allowMultiSelection) {

        if (dialogTitle == null) {
            dialogTitle = "select field";
        }

        PsiField[] fields = psiClass.getAllFields();
        PsiFieldMember[] fieldMembers = new PsiFieldMember[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldMembers[i] = new PsiFieldMember(fields[i]);
        }

        MemberChooser<PsiFieldMember> fieldChooser = new MemberChooser<>(fieldMembers,
            allowEmptySelection,
            allowMultiSelection,
            psiClass.getProject(),
            PsiUtil.isLanguageLevel5OrHigher(psiClass),
            new JPanel(new BorderLayout()));
        fieldChooser.setTitle(dialogTitle);
        fieldChooser.setCopyJavadocVisible(false);
        fieldChooser.show();
        List<PsiFieldMember> selectedElements = fieldChooser.getSelectedElements();
        if (CollectionUtils.isEmpty(selectedElements)) {
            return Lists.newArrayList();
        }
        return selectedElements
                           .stream()
                           .map(PsiElementClassMember::getElement)
                           .collect(Collectors.toList());
    }

    public static List<PsiMethod> selectMethods(@NotNull PsiClass psiClass,
        @Nullable String dialogTitle,
        boolean allowEmptySelection,
        boolean allowMultiSelection) {

        if (dialogTitle == null) {
            dialogTitle = "select method";
        }

        PsiMethod[] methods = psiClass.getAllMethods();
        PsiMethodMember[] methodMembers = new PsiMethodMember[methods.length];
        for (int i = 0; i < methods.length; i++) {
            methodMembers[i] = new PsiMethodMember(methods[i]);
        }

        MemberChooser<PsiMethodMember> methodChooser = new MemberChooser<>(methodMembers,
            allowEmptySelection,
            allowMultiSelection,
            psiClass.getProject(),
            PsiUtil.isLanguageLevel5OrHigher(psiClass),
            new JPanel(new BorderLayout()));
        methodChooser.setTitle(dialogTitle);
        methodChooser.setCopyJavadocVisible(false);
        methodChooser.show();
        List<PsiMethodMember> selectedElements = methodChooser.getSelectedElements();
        if (CollectionUtils.isEmpty(selectedElements)) {
            return Lists.newArrayList();
        }
        return selectedElements
            .stream()
            .map(PsiElementClassMember::getElement)
            .collect(Collectors.toList());
    }

    public static void writeToCaret(String content, @Nullable PsiFile file, @NotNull Editor editor) {

        final Project project;
        if (editor.getProject() != null) {
            project = editor.getProject();
        } else if (file != null) {
            project = file.getProject();
        }else{
            logger.error("write action failed, cannot get project from editor or file");
            return;
        }

        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();

        final int start = selectionModel.getSelectionStart();
        final int end = selectionModel.getSelectionEnd();

        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.replaceString(start, end, content);
            PsiDocumentManager.getInstance(project).commitDocument(document);
            if (file instanceof PsiJavaFile) {
                JavaCodeStyleManager.getInstance(project).shortenClassReferences(file);
            }
        });
        selectionModel.removeSelection();
    }

    private static void writeToEOF(String content, @NotNull PsiFile file) {
        List<GenerationInfo> generationInfoList = new ArrayList<>();
        // todo
        int offset = file.getTextRange().getEndOffset() - 1;
        GenerateMembersUtil.insertMembersAtOffset(file.getContainingFile(), offset, generationInfoList);
    }

}