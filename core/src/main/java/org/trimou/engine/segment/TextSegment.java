/*
 * Copyright 2013 Martin Kouba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.engine.segment;

import java.io.Writer;

import org.trimou.engine.context.ExecutionContext;

/**
 * Text segment.
 *
 * @author Martin Kouba
 */
public class TextSegment extends AbstractSegment {

	public TextSegment(String text, TemplateSegment template) {
		super(text, template);
	}

	public SegmentType getType() {
		return SegmentType.TEXT;
	}

	public void execute(Writer writer, ExecutionContext context) {
		write(writer, getText());
	}

	@Override
	public String getLiteralBlock() {
		return getText();
	}

}