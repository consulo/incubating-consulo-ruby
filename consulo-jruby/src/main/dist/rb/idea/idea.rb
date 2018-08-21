$application = com.intellij.openapi.application.ApplicationManager.getApplication unless $application
$actionManager = com.intellij.openapi.actionSystem.ActionManager.getInstance unless $actionManager
$intentionManager = com.intellij.codeInsight.intention.IntentionManager.getInstance unless $intentionManager
