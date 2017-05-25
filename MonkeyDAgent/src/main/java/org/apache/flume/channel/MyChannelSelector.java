package org.apache.flume.channel;/*
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.Configurables;
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

    public static final String CONFIG_PREFIX_CHANNELS = "channels"; //TODO refactor to "channels"
    //public static final String CONFIG_PREFIX_FILE_CHANNELS = "file_channels";
    public static final String CONFIG_IS_REQUIRED = "is_required";

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MyChannelSelector.class);

    private static final List<Channel> EMPTY_LIST = Collections.emptyList();


    private List<Channel> channels;
    //private List<Channel> fileChannels;
    private boolean isRequired = true;

    @Override
    public List<Channel> getRequiredChannels(Event event)
    {
        List<Channel> result = new ArrayList<Channel>();

//        //TODO
        for (Channel channel: channels)
        {
            if (channel instanceof MyMemoryChannel)
            {
                MyMemoryChannel t = (MyMemoryChannel) channel;
                if (t.getChannelCounter().getChannelFillPercentage() > 80)
                    continue;
            }
            //check size
            result.add(channel);
        }
        //LOG.info("channel num" + result.size());

        return result;


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
        //return EMPTY_LIST;
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
//        LOG.info("Channel Selector");
//        LOG.info("StackTrace", new Exception());
        Configurables.ensureRequiredNonNull(context, CONFIG_PREFIX_CHANNELS);

        Map<String, Channel> channelNameMap = getChannelNameMap();

        LOG.info("RUA memory_channels:" + context.getString(CONFIG_PREFIX_CHANNELS));
        LOG.info("RUA channel map size:" + getChannelNameMap().size());


        channels = getChannelListFromNames(context.getString(CONFIG_PREFIX_CHANNELS), channelNameMap);
        //fileChannels = getChannelListFromNames(context.getString(CONFIG_PREFIX_FILE_CHANNELS), channelNameMap);

        try
        {
            isRequired = context.getBoolean(CONFIG_IS_REQUIRED);
        }
        catch (Exception e)
        {
            LOG.info(" error when loading isRequired", e.getMessage());
            isRequired = true;
        }


        LOG.info("memory_channel_num:" + channels.size());
        //LOG.info("file_channel_num:" + fileChannels.size());
        LOG.info("isRequired:" + isRequired);

    }

}
