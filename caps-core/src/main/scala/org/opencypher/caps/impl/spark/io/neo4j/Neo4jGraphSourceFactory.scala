/*
 * Copyright (c) 2016-2017 "Neo4j, Inc." [https://neo4j.com]
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
package org.opencypher.caps.impl.spark.io.neo4j

import java.net.{URI, URLDecoder}

import org.apache.http.client.utils.URIBuilder
import org.neo4j.driver.v1.Config
import org.opencypher.caps.api.spark.io.CAPSGraphSourceFactoryCompanion
import org.opencypher.caps.api.spark.CAPSSession
import org.opencypher.caps.impl.spark.io.CAPSGraphSourceFactoryImpl
import org.opencypher.caps.impl.spark.exception.Raise
import org.opencypher.caps.impl.spark.io.neo4j.external.Neo4jConfig

case object Neo4jGraphSourceFactory extends CAPSGraphSourceFactoryCompanion("bolt", "bolt+routing")

case class Neo4jGraphSourceFactory()
  extends CAPSGraphSourceFactoryImpl[Neo4jGraphSource](Neo4jGraphSourceFactory) {

  override protected def sourceForURIWithSupportedScheme(uri: URI)(implicit capsSession: CAPSSession): Neo4jGraphSource = {
    val (user, passwd) = getUserInfo(uri)
    val boltUri = new URIBuilder()
      .setScheme(uri.getScheme)
      .setUserInfo(uri.getUserInfo)
      .setHost(uri.getHost)
      .setPort(uri.getPort)
      .build()

    val neo4jConfig = Neo4jConfig(boltUri, user, passwd, encrypted = false)
    Neo4jGraphSource(neo4jConfig, getQueries(uri))
  }

  private def getUserInfo(uri: URI) = uri.getUserInfo match {
    case null => "" -> None

    case info =>
      val tokens = info.split(":")
      if (tokens.size != 2) Raise.invalidArgument("username:password", "nothing")
      tokens(0) -> Some(tokens(1))
  }

  private def getQueries(uri: URI) = uri.getQuery match {
    case null => None

    case queries =>
      val tokens = queries.split(";")
      val nodeQuery = tokens.headOption.getOrElse(Raise.invalidArgument("a node query", "none"))
      val relQuery = tokens.tail.headOption.getOrElse(Raise.invalidArgument("a relationship query", "none"))
      Some(URLDecoder.decode(nodeQuery, "UTF-8") -> URLDecoder.decode(relQuery, "UTF-8"))
  }
}
