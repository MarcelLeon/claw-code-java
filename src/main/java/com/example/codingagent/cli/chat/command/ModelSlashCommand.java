package com.example.codingagent.cli.chat.command;

import com.example.codingagent.model.ModelCommandSnapshot;
import com.example.codingagent.model.ModelSelectionResult;
import com.example.codingagent.model.ModelSettingService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 查看或切换当前 chat 会话模型。
 */
@Component
public class ModelSlashCommand implements ChatSlashCommand {

    private final ModelSettingService modelSettingService;

    public ModelSlashCommand(ModelSettingService modelSettingService) {
        this.modelSettingService = modelSettingService;
    }

    @Override
    public String name() {
        return "model";
    }

    @Override
    public String description() {
        return "查看或切换当前会话模型设置";
    }

    @Override
    public ChatSlashCommandResult execute(ChatSlashCommandRequest request) {
        String argument = request.argument().trim();
        if (argument.isBlank()) {
            return ChatSlashCommandResult.output(renderPicker(request));
        }
        if (modelSettingService.isHelpArgument(argument)) {
            return ChatSlashCommandResult.output(List.of(
                    "Run /model to open the model selection menu, or /model [modelName] to set the model.",
                    "Use /model default to reset to the provider default model.",
                    "Use /model current to show the current effective model."
            ));
        }
        if (modelSettingService.isInfoArgument(argument)) {
            return ChatSlashCommandResult.output(renderCurrent(request));
        }
        if ("default".equalsIgnoreCase(argument)) {
            request.sessionState().updateModel(null);
            ModelCommandSnapshot snapshot = modelSettingService.snapshot(
                    request.sessionState().provider(),
                    request.sessionState().model()
            );
            return ChatSlashCommandResult.output(List.of("Set model to " + snapshot.effectiveModel() + " (default)"));
        }
        ModelSelectionResult selection = modelSettingService.select(request.sessionState().provider(), argument);
        request.sessionState().updateModel(selection.modelSetting());
        return ChatSlashCommandResult.output(List.of("Set model to " + selection.displayLabel()));
    }

    private List<String> renderPicker(ChatSlashCommandRequest request) {
        ModelCommandSnapshot snapshot = modelSettingService.snapshot(
                request.sessionState().provider(),
                request.sessionState().model()
        );
        List<String> lines = new ArrayList<>();
        lines.add("Model selection menu (text mode):");
        lines.add("Current model: " + formatCurrentModel(snapshot));
        lines.add("Default model for provider " + snapshot.provider() + ": " + snapshot.defaultModel());
        lines.add("Preset models:");
        snapshot.presetModels().forEach(model -> lines.add("- " + model));
        lines.add("Use /model <modelName> to switch, /model default to reset, /model help for usage.");
        return lines;
    }

    private List<String> renderCurrent(ChatSlashCommandRequest request) {
        ModelCommandSnapshot snapshot = modelSettingService.snapshot(
                request.sessionState().provider(),
                request.sessionState().model()
        );
        return List.of(
                "Current model: " + formatCurrentModel(snapshot),
                "Default model for provider " + snapshot.provider() + ": " + snapshot.defaultModel()
        );
    }

    private String formatCurrentModel(ModelCommandSnapshot snapshot) {
        if (snapshot.usesDefaultModel()) {
            return snapshot.effectiveModel() + " (default)";
        }
        return snapshot.effectiveModel();
    }
}
