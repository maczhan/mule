/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing.response;

import org.mule.DefaultMuleEvent;
import org.mule.RequestContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.routing.RoutingException;
import org.mule.api.service.Service;
import org.mule.routing.EventGroup;
import org.mule.routing.correlation.EventCorrelator;
import org.mule.routing.correlation.EventCorrelatorCallback;

/**
 * <code>AbstractResponseAggregator</code> provides a base class for implementing
 * response aggregator routers. This provides a thread-safe implemenetation and
 * allows developers to customise how and when events are grouped and collated.
 * Response Agrregators are used to collect responses that are usually sent to
 * replyTo endpoints set on outbound routers. When an event is sent out via an
 * outbound router, the response router will block the response flow on an
 * Service until the Response Router resolves a reply or times out.
 */
public abstract class AbstractResponseAggregator extends AbstractResponseRouter
{
    private int timeout = -1; // undefined

    private boolean failOnTimeout = true;

    private EventCorrelator eventCorrelator;

    @Override
    public void initialise() throws InitialisationException
    {
        if (timeout == -1) // undefined
        {
            setTimeout(muleContext.getConfiguration().getDefaultResponseTimeout());
        }
        eventCorrelator = new EventCorrelator(getCorrelatorCallback(), null, getMessageInfoMapping(), muleContext);
        eventCorrelator.setTimeout(getTimeout());
        eventCorrelator.setFailOnTimeout(isFailOnTimeout());
        super.initialise();
    }

    protected EventCorrelator getEventCorrelator()
    {
        return eventCorrelator;
    }

    public void process(MuleEvent event) throws RoutingException
    {
        if (!(event.getFlowConstruct() instanceof Service))
        {
            throw new UnsupportedOperationException("EventAggregator is only supported with Service");
        }
        
        eventCorrelator.addEvent(event);
    }

    /**
     * This method is called by the responding callee thread and should return the
     * aggregated response message
     *
     * @param message
     * @throws RoutingException
     */
    public MuleEvent getResponse(MuleEvent event) throws RoutingException
    {
        MuleMessage response = eventCorrelator.getResponse(event.getMessage());
        MuleMessage safe = RequestContext.safeMessageCopy(response);
        return response != null ? new DefaultMuleEvent(safe, event) : null;
    }

    public boolean isFailOnTimeout()
    {
        return failOnTimeout;
    }

    public void setFailOnTimeout(boolean failOnTimeout)
    {
        this.failOnTimeout = failOnTimeout;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    protected abstract EventCorrelatorCallback getCorrelatorCallback();

    protected MuleMessage aggregateEvents(EventGroup events) throws RoutingException
    {
        return null;
    }
}
