package com.axr.rplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.terminal.JBTerminalWidget;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory;
import org.jetbrains.plugins.terminal.TerminalView;

/**
 * @Author xinrui.an
 * @Date 2025/3/13
 */
public class ReportAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        // 尝试直接从事件数据中获取当前选中文件
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        // 如果直接获取不到，再从 Editor 中获取 Document 对应的 VirtualFile
        if (file == null) {
            Editor editor = e.getData(CommonDataKeys.EDITOR);
            if (editor != null) {
                file = FileDocumentManager.getInstance().getFile(editor.getDocument());
            }
        }
        if (file != null) {
            // 获取全局路径
            String fullPath = file.getPath();
            Messages.showMessageDialog(project, "当前文件全局路径为：\n" + fullPath, "文件路径", Messages.getInformationIcon());
            
            // 打开终端并运行命令
            openTerminalAndRunCommand(project, "ls");
            
            // 如果是.rkt文件，还可以添加运行racket的命令
            if (file.getName().endsWith(".rkt")) {
                openTerminalAndRunCommand(project, "racket " + fullPath);
            }
        } else {
            Messages.showMessageDialog(project, "未能找到当前文件。", "错误", Messages.getErrorIcon());
        }
    }
    
    /**
     * 打开终端并运行指定命令
     * @param project 当前项目
     * @param command 要运行的命令
     */
    private void openTerminalAndRunCommand(Project project, String command) {
        try {
            // 获取或打开终端工具窗口
            ToolWindow terminalToolWindow = ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID);
            if (terminalToolWindow != null) {
                terminalToolWindow.activate(() -> {
                    try {
                        // 获取终端视图
                        TerminalView terminalView = TerminalView.getInstance(project);
                        // 获取当前终端小部件
                        ShellTerminalWidget widget = terminalView.getTerminalWidget();
                        
                        // 如果没有打开的终端，创建一个新的
                        if (widget == null) {
                            widget = terminalView.createLocalShellWidget(project.getBasePath(), "Terminal");
                        }
                        
                        // 运行命令
                        if (widget != null) {
                            widget.executeCommand(command);
                        }
                    } catch (Exception ex) {
                        Messages.showErrorDialog(project, "无法在终端中执行命令: " + ex.getMessage(), "错误");
                    }
                }, true);
            }
        } catch (Exception e) {
            Messages.showErrorDialog(project, "无法打开终端: " + e.getMessage(), "错误");
        }
    }
}
