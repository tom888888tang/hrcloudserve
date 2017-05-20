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
package odata.service.user_score.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

import persistence.UserBean;

/**
 * this class is supposed to declare the metadata of the OData service
 * it is invoked by the Olingo framework e.g. when the metadata document of the service is invoked
 * e.g. http://localhost:8080/ExampleService1/ExampleService1.svc/$metadata
 */
public class EdmProvider extends CsdlAbstractEdmProvider {

  // Service Namespace
  public static final String NAMESPACE = "OData.service.user";

  // EDM Container
  public static final String CONTAINER_NAME = "Container";
  public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

  // Entity Types Names
  public static final String ET_NAME = "User_score";
  public static final FullQualifiedName ET_FQN = new FullQualifiedName(NAMESPACE, ET_NAME);

  // Entity Set Names
  public static final String ES_NAME = "User_score";
  private UserBean userBean;

  @Override
  public List<CsdlSchema> getSchemas() {

    // create Schema
    CsdlSchema schema = new CsdlSchema();
    schema.setNamespace(NAMESPACE);

    // add EntityTypes
    List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
    entityTypes.add(getEntityType(ET_FQN));
    schema.setEntityTypes(entityTypes);

    // add EntityContainer
    schema.setEntityContainer(getEntityContainer());

    // finally
    List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
    schemas.add(schema);

    return schemas;
  }


  @Override
  public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {

    // this method is called for one of the EntityTypes that are configured in the Schema
    if(entityTypeName.equals(ET_FQN)){

      //create EntityType properties
      CsdlProperty user_id = new CsdlProperty().setName("User_id").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty course_id = new CsdlProperty().setName("Course_id").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty course_version = new CsdlProperty().setName("Course_version").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty score = new CsdlProperty().setName("Score").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty title = new CsdlProperty().setName("Title").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty course_type = new CsdlProperty().setName("Course_type").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());     
      CsdlProperty wechat_nickname = new CsdlProperty().setName("Wechat_nickname").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty wechat_img = new CsdlProperty().setName("Wechat_img").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()); 
      CsdlProperty region = new CsdlProperty().setName("Region").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty department = new CsdlProperty().setName("Department").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()); 
      CsdlProperty position = new CsdlProperty().setName("Position").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty manager = new CsdlProperty().setName("Manager").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()); 
           
      // configure EntityType
      CsdlEntityType entityType = new CsdlEntityType();
      entityType.setName(ET_NAME);
      entityType.setProperties(Arrays.asList(user_id, course_id , course_version, score, title , course_type, wechat_nickname, wechat_img,region,department,position,manager));
      //set key
      CsdlPropertyRef propertyRef = new CsdlPropertyRef();
      propertyRef.setName("User_id");
      entityType.setKey(Collections.singletonList(propertyRef));
/*			propertyRef.setName("Course_id");
			entityType.setKey(Collections.singletonList(propertyRef));
			propertyRef.setName("Course_version");
			entityType.setKey(Collections.singletonList(propertyRef));*/

      return entityType;
    }

    return null;
  }

  @Override
  public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {

    if(entityContainer.equals(CONTAINER)){
      if(entitySetName.equals(ES_NAME)){
        CsdlEntitySet entitySet = new CsdlEntitySet();
        entitySet.setName(ES_NAME);
        entitySet.setType(ET_FQN);

        return entitySet;
      }
    }

    return null;
  }

  @Override
  public CsdlEntityContainer getEntityContainer() {

    // create EntitySets
    List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
    entitySets.add(getEntitySet(CONTAINER, ES_NAME));

    // create EntityContainer
    CsdlEntityContainer entityContainer = new CsdlEntityContainer();
    entityContainer.setName(CONTAINER_NAME);
    entityContainer.setEntitySets(entitySets);

    return entityContainer;
  }

  @Override
  public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {

    // This method is invoked when displaying the service document at e.g. http://localhost:8080/DemoService/DemoService.svc
    if(entityContainerName == null || entityContainerName.equals(CONTAINER)){
      CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
      entityContainerInfo.setContainerName(CONTAINER);
      return entityContainerInfo;
    }

    return null;
  }
}
