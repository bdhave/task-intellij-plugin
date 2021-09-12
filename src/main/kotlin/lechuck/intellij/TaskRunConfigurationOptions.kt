package lechuck.intellij

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.openapi.components.StoredProperty
import com.intellij.openapi.components.StoredPropertyBase

class TaskRunConfigurationOptions : RunConfigurationOptions() {
    private val taskPath: StoredProperty<String?> = string("").provideDelegate(this, "taskPath")
    private val taskfile: StoredProperty<String?> = string("").provideDelegate(this, "taskfile")
    private val task: StoredProperty<String?> = string("").provideDelegate(this, "task")
    private val arguments: StoredProperty<String?> = string("").provideDelegate(this, "arguments")
    private val mapStoredPropertyBase: StoredPropertyBase<MutableMap<String, String>> = map()
    private val environments: StoredProperty<MutableMap<String, String>> =
        mapStoredPropertyBase.provideDelegate(this, "environments")

    fun getTaskPath(): String? {
        return taskPath.getValue(this)
    }

    fun setTaskPath(taskPath: String?) {
        this.taskPath.setValue(this, taskPath)
    }

    fun getTaskfile(): String? {
        return taskfile.getValue(this)
    }

    fun setTaskfile(taskfile: String?) {
        this.taskfile.setValue(this, taskfile)
    }

    fun getTask(): String? {
        return task.getValue(this)
    }

    fun setTask(task: String?) {
        this.task.setValue(this, task)
    }

    fun getArguments(): String? {
        return arguments.getValue(this)
    }

    fun setArguments(arguments: String?) {
        this.arguments.setValue(this, arguments)
    }

    fun getEnvironments(): EnvironmentVariablesData {
        val map = environments.getValue(this)
        return EnvironmentVariablesData.create(map, true)
    }

    fun setEnvironments(env: EnvironmentVariablesData) {
        environments.setValue(this, env.envs)
    }
}