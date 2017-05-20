/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package odata.service.user_score.web;

import java.io.IOException;
import java.lang.Override;import java.lang.RuntimeException;import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import odata.service.login.util.JWTFactory;
import odata.service.user_score.data.Storage;
import odata.service.user_score.service.EdmProvider;
import odata.service.user_score.service.UserEntityCollectionProcessor;
import odata.service.user_score.service.UserEntityProcessor;
import odata.service.user_score.service.UserPrimitiveProcessor;
import odata.service.util.Util;

public class User_ScoreUpdateOdataServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(User_ScoreUpdateOdataServlet.class);


  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     Util.update_score();
  }

}
