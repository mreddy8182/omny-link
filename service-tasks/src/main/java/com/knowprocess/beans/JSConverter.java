/*******************************************************************************
 *Copyright 2011-2018 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.knowprocess.beans;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;

public class JSConverter implements GenericConverter,
        Converter<Object, Object> {
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(JSConverter.class);
    private String script;
    private ScriptEngine engine;
    private Class<?> targetType;
    private Class<?> sourceType;

    public <S, T> JSConverter(Class<S> sourceType, Class<T> targetType, String script) {
        super();
        LOGGER.info("Define converter using script:\n" + script);
        this.script = script;
        this.sourceType = sourceType;
        this.targetType = targetType;

        initScriptEngine();
    }

    protected ScriptEngine initScriptEngine() {
        long start = System.currentTimeMillis();
        if (engine == null) {
            ScriptEngineManager factory = new ScriptEngineManager();

            if (LOGGER.isDebugEnabled()) {
                List<ScriptEngineFactory> engineFactories = factory
                        .getEngineFactories();
                for (ScriptEngineFactory scriptEngineFactory : engineFactories) {
                    LOGGER.debug(" Available factory: " + scriptEngineFactory);
                }
            }

            engine = factory.getEngineByName("JavaScript");
        }
        LOGGER.info("initScriptEngine took: "
                + (System.currentTimeMillis() - start));
        return engine;
    }

    @Override
    public Object convert(Object source) {
        long start = System.currentTimeMillis();
        Bindings bindings = engine.createBindings();
        bindings.put("src", source);
        Object obj = null;
        try {
            engine.eval(script, bindings);
        } catch (ScriptException e) {
            throw new ConversionNotSupportedException(source, targetType, e);
        }
        obj = bindings.get("o");
        LOGGER.info("eval script converter took (ms): "
                + (System.currentTimeMillis() - start));
        return obj;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(this.sourceType,
                this.targetType));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType,
            TypeDescriptor targetType) {
        return convert(source);
    }

}
