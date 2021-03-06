/*******************************************************************************
 * Copyright 2014 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ibm.hrl.proton.context.metadata;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.hrl.proton.context.management.SegmentationValue;
import com.ibm.hrl.proton.expression.eep.EepExpression;
import com.ibm.hrl.proton.metadata.context.SegmentationContextType;
import com.ibm.hrl.proton.metadata.context.interfaces.ISegmentationContextType;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;

/**
 * Comprises several single segmentation contexts, all related to the same composite context.
 * All segmentation contexts combinations (including the trivial one with a single segment) are
 * maintained as ComposedSegmentation, e.g., global and local segmentation of the
 * context-agent pair.
 * <code>ComposedSegmentation</code>.
 * 
 */
public class ComposedSegmentation {
	
	protected Collection<SegmentationContextType> segments;	

	private static Logger logger = LoggerFactory.getLogger(ComposedSegmentation.class.getName());

	public ComposedSegmentation() {
		segments = new ArrayList<SegmentationContextType>();
	}
	
	public ComposedSegmentation(Collection<ISegmentationContextType> segmentation) {
		segments = new ArrayList<SegmentationContextType>();
		for (ISegmentationContextType segment: segmentation) {
			segments.add((SegmentationContextType)segment);
		}
	}
	
	public void add(SegmentationContextType segmentation) {
		segments.add(segmentation);
	}

	public Collection<SegmentationContextType> getSegments() {
		return segments;
	}
    /**
     * Returns the value of this segmentation for a given event instance.
     * Event can either participate in a single segmentation or not (participate partially);
     * for segments the event participates in, a list of values id calculated.
	 * @param 	event
     * @return 	SegmentationValue
     */   
	public SegmentationValue getSegmentationValue(IEventInstance event) {
		// evaluate value of this segment for the given event
				// if event does not contain attributes complying with the composite context
				// return an empty SegmentationValue
				//logger.debug("getSegmentationValue: for event"+event);
				// we assume that event attributes comply with given segment (defs parsing check)
				SegmentationValue value = new SegmentationValue(this);
				for (SegmentationContextType segment: segments) {
					//logger.debug("getSegmentationValue: iterating over segments"+segment);
					//logger.debug("getSegmentationValue: getting parsed expression for event type"+ event.getEventType());
					EepExpression expression = (EepExpression)segment.getParsedSegmentationExpression(event.getEventType());
					
					if (expression != null) { // event can either participate in this segment or not
						// invoke eep to evaluate expression for the given event instance
						Object expressionResult = expression.copyAndEvaluate(event);
						String expressionValue = expressionResult.toString();
						value.addValue(segment.getId(),expressionValue);
					}
				}				
				return value;
	}
	
}
