package com.fnet.common.net;

import com.fnet.common.tool.NetTool;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NotLinuxCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !NetTool.isLinuxEnvironment();
    }
}
