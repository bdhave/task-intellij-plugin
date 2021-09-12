package lechuck.intellij

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import lechuck.intellij.util.RegexUtil

internal class TaskCommandLineState(
    env: ExecutionEnvironment,
    private val cfg: TaskRunConfiguration,
) : CommandLineState(env) {

    override fun startProcess(): ProcessHandler {
        val handler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(createGeneralCommandLine())
        ProcessTerminatedListener.attach(handler)
        return handler
    }

    private fun createGeneralCommandLine(): GeneralCommandLine {
        val options = cfg.getTaskRunConfigurationOptions()
        val cmd = mutableListOf<String>()

        // taskPath
        val taskPath = options.getTaskPath()
        cmd.add(if (taskPath?.isNotEmpty() == true) taskPath else "task")

        // taskfile
        val taskfile = options.getTaskfile()
        if (taskfile?.isNotEmpty() == true) {
            cmd.addAll(listOf("--taskfile", taskfile))
        }

        // task
        val task = options.getTask()
        if (task?.isNotEmpty() == true) {
            cmd.add(task)
        } else {
            throw IllegalArgumentException("task it empty")
        }

        // arguments
        val arguments = options.getArguments()
        val argumentList = RegexUtil.splitBySpace(arguments)
        if (argumentList?.isNotEmpty() == true) {
            cmd.add("--")
            cmd.addAll(argumentList)
        }

        // environment variables
        val envMap = mutableMapOf<String, String>()
        return GeneralCommandLine(cmd).withEnvironment(envMap)
    }
}