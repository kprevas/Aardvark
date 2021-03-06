/*
 * Copyright (c) 2010 Guidewire Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gw.vark;

import gw.lang.reflect.TypeSystem;
import gw.util.GosuExceptionUtil;
import gw.util.StreamUtil;
import org.apache.tools.ant.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AntCoreTasksCustomTypeInfo extends AbstractTaskCustomTypeInfo {
  private final AntCoreTasks _instance = new AntCoreTasks();

  public AntCoreTasksCustomTypeInfo() {
    super(TypeSystem.get(AntCoreTasks.class));
    addTasksAsMethods(getTasks());
  }

  @Override
  protected Object getOwnerInstance() {
    return _instance;
  }

  private HashMap<String, Class<? extends Task>> getTasks() {
    HashMap<String, Class<? extends Task>> tasks = new HashMap<String, Class<? extends Task>>();
    for (Map.Entry entry : readTaskListing().entrySet()) {
      try {
        String taskName = (String) entry.getKey();
        String taskClassName = (String) entry.getValue();
        //noinspection unchecked
        Class<? extends Task> taskClass = (Class<? extends Task>) Class.forName(taskClassName);
        tasks.put(taskName, taskClass);
      } catch (ClassNotFoundException e) {
        //System.err.println("Class not found for task " + taskClassName);
      } catch (NoClassDefFoundError e) {
        //System.err.println("caught NCDFE while loading " + taskClassName);
      }
    }
    return tasks;
  }

  private Properties readTaskListing() {
    Properties tasks = new Properties();
    URL listingResource = Thread.currentThread().getContextClassLoader().getResource("org/apache/tools/ant/taskdefs/defaults.properties");
    InputStream in = null;
    try {
      in = listingResource.openStream();
      tasks.load(in);
      return tasks;
    } catch (IOException e) {
      throw GosuExceptionUtil.forceThrow(e);
    } finally {
      try {
        StreamUtil.close(in);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
