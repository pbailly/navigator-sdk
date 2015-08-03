/*
 * Copyright (c) 2015 Cloudera, Inc.
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

package com.cloudera.nav.plugin.examples.tags;

import com.cloudera.nav.plugin.client.NavApiCient;
import com.cloudera.nav.plugin.client.NavigatorPlugin;
import com.cloudera.nav.plugin.model.Source;
import com.cloudera.nav.plugin.model.SourceType;
import com.cloudera.nav.plugin.model.entities.EntityType;
import com.cloudera.nav.plugin.model.entities.HdfsEntity;
import com.google.common.collect.Sets;

/**
 * Tagging HDFS Files and Directories
 *
 * Tags is an important part of business metadata. This example uses the
 * Navigator plugin to tag an HDFS directory as sensitive.
 * Users and applications can then the tags to trigger actions such as
 * encryption, masking, and/or restrictions to permissions.
 */
public class SetHdfsFileTags {

  public static void main(String[] args) {

    // setup the plugin and api client
    NavigatorPlugin plugin = NavigatorPlugin.fromConfigFile(args[0]);
    NavApiCient client = plugin.getClient();
    Source fs = client.getOnlySource(SourceType.HDFS);

    // send tags for multiple entities to Navigator
    HdfsEntity dir = new HdfsEntity("/user/hdfs", EntityType.DIRECTORY,
        fs.getIdentity());
    dir.setTags(Sets.newHashSet("HAS_SENSITIVE_FILES",
        "CONTAINS_SOME_SUPER_SECRET_STUFF"));

    plugin.write(dir);
  }
}
