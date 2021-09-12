package lechuck.intellij

import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project

class TaskRunConfiguration(project: Project, factory: ConfigurationFactory?, name: String?) :
    RunConfigurationBase<TaskRunConfigurationOptions?>(project, factory, name) {

    fun getTaskRunConfigurationOptions(): TaskRunConfigurationOptions {
        return super.getOptions() as TaskRunConfigurationOptions
    }

    fun getTaskPath(): String? {
        return getTaskRunConfigurationOptions().getTaskPath()
    }

    fun setTaskPath(taskPath: String?) {
        getTaskRunConfigurationOptions().setTaskPath(taskPath)
    }

    fun getTaskfile(): String? {
        return getTaskRunConfigurationOptions().getTaskfile()
    }

    fun setTaskfile(taskfile: String?) {
        getTaskRunConfigurationOptions().setTaskfile(taskfile)
    }

    fun getTask(): String? {
        return getTaskRunConfigurationOptions().getTask()
    }

    fun setTask(task: String?) {
        getTaskRunConfigurationOptions().setTaskfile(task)
    }

    fun getArguments(): String? {
        return getTaskRunConfigurationOptions().getArguments()
    }

    fun setArguments(arguments: String?) {
        getTaskRunConfigurationOptions().setTaskfile(arguments)
    }

    fun getEnvironments(): EnvironmentVariablesData {
        return getTaskRunConfigurationOptions().getEnvironments()
    }

    fun setEnvironments(env: EnvironmentVariablesData) {
        getTaskRunConfigurationOptions().setEnvironments(env)
    }

    override fun getConfigurationEditor(): TaskSettingsEditor {
        return TaskSettingsEditor(this.project)
    }

    override fun checkConfiguration() {
        if (this.getTask()?.isNotEmpty() == false) {
            throw RuntimeConfigurationError("Task is not set")
        }
    }

    override fun getState(executor: Executor, env: ExecutionEnvironment): RunProfileState {
        return TaskCommandLineState(env, this)
    }
}