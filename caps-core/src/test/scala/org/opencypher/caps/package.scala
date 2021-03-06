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
package org.opencypher

import org.opencypher.caps.api.expr.Var
import org.opencypher.caps.api.schema.Schema
import org.opencypher.caps.ir.api.{IRField, IRNamedGraph}
import org.opencypher.caps.api.types.CypherType

import scala.language.implicitConversions

package object caps {
  implicit def toVar(s: Symbol): Var = Var(s.name)()
  implicit def toVar(t: (Symbol, CypherType)): Var = Var(t._1.name)(t._2)
  implicit def toField(s: Symbol): IRField = IRField(s.name)()
  implicit def toField(t: (Symbol, CypherType)): IRField = IRField(t._1.name)(t._2)
  implicit def toGraph(s: Symbol): IRNamedGraph = IRNamedGraph(s.name, Schema.empty)
}
