package lechuck.intellij

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.PathMacros
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.FixedSizeButton
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UIUtil
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.defaultIfEmpty
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class TaskSettingsEditor(private val project: Project) : SettingsEditor<TaskRunConfiguration>() {
    private val panel: JPanel by lazy {
        FormBuilder.createFormBuilder()
            .setAlignLabelOnRight(false)
            .setHorizontalGap(UIUtil.DEFAULT_HGAP)
            .setVerticalGap(UIUtil.DEFAULT_VGAP)
            .addLabeledComponent("Task executable", taskExecutableField)
            .addLabeledComponent("Taskfile", filenameField)
            .addLabeledComponent("Task", taskField)
            .addComponent(LabeledComponent.create(argumentsField, "CLI arguments"))
            .panel
    }
    private val taskExecutableField = TextFieldWithBrowseButton()
    private val filenameField = TextFieldWithBrowseButton()
    private val taskCompletionProvider = TextFieldWithAutoCompletion.StringsCompletionProvider(emptyList(), null)
    private val taskField = TextFieldWithAutoCompletion(project, taskCompletionProvider, true, "")
    private val argumentsField = ExpandableTextField()
    private val envVarsComponent = EnvironmentVariablesComponent()
    private val mapper = ObjectMapper(YAMLFactory()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    init {
        taskExecutableField.addBrowseFolderListener(
            "Task Executable",
            "Select task executable to use",
            project,
            TaskExecutableFileChooserDescriptor()
        )

        filenameField.addBrowseFolderListener(
            "Taskfile",
            "Select Taskfile.yml to run",
            project,
            TaskfileFileChooserDescriptor()
        )

        filenameField.textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                textChanged()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                textChanged()
            }

            override fun changedUpdate(e: DocumentEvent?) {
                textChanged()
            }

            private fun textChanged() {
                updateTargetCompletion(filenameField.text)
            }
        })
    }

    private fun updateTargetCompletion(filename: String) {
        val file = LocalFileSystem.getInstance().findFileByPath(filename)
        if (file != null) {
            val psiFile = PsiManager.getInstance(project).findFile(file)
            if (psiFile != null) {
                taskCompletionProvider.setItems(findTasks(psiFile))
                return
            }
        }
        taskCompletionProvider.setItems(emptyList())
    }

    private fun findTasks(file: PsiFile): Collection<String> {
        try {
            file.virtualFile.inputStream.use { `is` ->
                val taskfile: Taskfile = mapper.readValue(`is`, Taskfile::class.java)
                val tasks = taskfile.tasks
                return tasks?.keys ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    override fun resetEditorFrom(cfg: TaskRunConfiguration) {
        taskExecutableField.text = defaultIfEmpty(cfg.getTaskPath(), "")
        filenameField.text = defaultIfEmpty(cfg.getTaskfile(), "")
        taskField.text = defaultIfEmpty(cfg.getTask(), "")
        argumentsField.text = cfg.getArguments()
        envVarsComponent.envData = cfg.getEnvironments()
    }

    override fun applyEditorTo(cfg: TaskRunConfiguration) {
        cfg.setTaskPath(taskExecutableField.text)
        cfg.setTaskfile(filenameField.text)
        cfg.setTask(taskField.text)
        cfg.setArguments(argumentsField.text)
        cfg.setEnvironments(envVarsComponent.envData)
    }

    override fun createEditor(): JComponent {
        return panel
    }

    private fun createComponentWithMacroBrowse(textAccessor: TextFieldWithBrowseButton): JComponent {
        // TODO replace TextFieldWithBrowseButton with this
        val button = FixedSizeButton(textAccessor)
        button.icon = AllIcons.Actions.ListFiles
        button.addActionListener { e ->
            val userMacroNames = ArrayList(PathMacros.getInstance().userMacroNames)
            JBPopupFactory.getInstance()
                .createPopupChooserBuilder(userMacroNames)
                .setItemChosenCallback { textAccessor.setText("$\$item$") }
                .setMovable(false)
                .setResizable(false)
                .createPopup()
                .showUnderneathOf(button)
        }
        val p = JPanel(BorderLayout())
        p.add(textAccessor, BorderLayout.CENTER)
        p.add(button, BorderLayout.EAST)
        return p
    }

}