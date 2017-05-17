package monkey.d.channel;/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.channel.AbstractChannelSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyChannelSelector extends AbstractChannelSelector
{

//    public static final String CONFIG_MULTIPLEX_HEADER_NAME = "header";
//    public static final String DEFAULT_MULTIPLEX_HEADER =
//            "flume.selector.header";
//    public static final String CONFIG_PREFIX_MAPPING = "mapping.";
//    public static final String CONFIG_DEFAULT_CHANNEL = "default";
//    public static final String CONFIG_PREFIX_OPTIONAL = "optional";

    public static final String CONFIG_PREFIX_MEMORY_CHANNELS = "mem_channels";
    public static final String CONFIG_PREFIX_FILE_CHANNELS = "file_channels";
    public static final String CONFIG_IS_REQUIRED = "is_required";

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MyChannelSelector.class);

    private static final List<Channel> EMPTY_LIST = Collections.emptyList();


    private List<Channel> memChannels;
    private List<Channel> fileChannels;
    private boolean isRequired = true;

    @Override
    public List<Channel> getRequiredChannels(Event event)
    {
//        String headerValue = event.getHeaders().get(headerName);
//        if (headerValue == null || headerValue.trim().length() == 0)
//        {
//            return defaultChannels;
//        }
//
//        List<Channel> channels = channelMapping.get(headerValue);
//
//
//
//        //This header value does not point to anything
//        //Return default channel(s) here.
//        if (channels == null)
//        {
//            channels = defaultChannels;
//        }
//
//        return channels;
        return EMPTY_LIST;
    }

    @Override
    public List<Channel> getOptionalChannels(Event event)
    {
//        String hdr = event.getHeaders().get(headerName);
//        List<Channel> channels = optionalChannels.get(hdr);
//
//        if (channels == null)
//        {
//            channels = EMPTY_LIST;
//        }
//        return channels;
        return EMPTY_LIST;
    }

    @Override
    public void configure(Context context)
    {
        LOG.info("Channel Selector");
        LOG.info("StackTrace", new Exception());


        Map<String, Channel> channelNameMap = getChannelNameMap();

        memChannels = getChannelListFromNames(context.getString(CONFIG_PREFIX_MEMORY_CHANNELS), channelNameMap);
        fileChannels = getChannelListFromNames(context.getString(CONFIG_PREFIX_FILE_CHANNELS), channelNameMap);

        try
        {
            isRequired = context.getBoolean(CONFIG_IS_REQUIRED);
        }
        catch (Exception e)
        {
            LOG.info(" error when loading isRequired", e.getMessage());
            isRequired = true;
        }


        LOG.info("memory_channel_num:" + memChannels.size());
        LOG.info("file_channel_num:" + fileChannels.size());
        LOG.info("isRequired:" + isRequired);

    }

}