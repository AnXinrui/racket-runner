package com.axr.rplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.terminal.ui.TerminalWidget;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;

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
//        if (file != null) {
//            // 获取全局路径
//            String fullPath = file.getPath();
//            Messages.showMessageDialog(project, "当前文件全局路径为：\n" + fullPath, "文件路径", Messages.getInformationIcon());
//        } else {
//            Messages.showMessageDialog(project, "未能找到当前文件。", "错误", Messages.getErrorIcon());
//        }


        // 获取当前项目的 TerminalView 实例
        TerminalToolWindowManager terminalView = TerminalToolWindowManager.getInstance(project);
        // 使用项目根目录作为终端的工作目录
        String workingDirectory = project.getBasePath();
        // 创建一个新的本地终端标签页，标签标题可以自定义
        TerminalWidget shellWidget = terminalView.createShellWidget(workingDirectory, "Terminal - ls", true, false);
        // 执行命令 "ls"（注意：在 Windows 系统下可能需要替换为 dir）
        try {
            shellWidget.sendCommandToExecute("ls");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        Messages.showMessageDialog("over", "header", Messages.getInformationIcon());

    }
}
