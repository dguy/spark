/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.sources

import org.apache.spark.sql.{Row, QueryTest}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.test.TestSQLContext
import org.apache.spark.util.Utils

class PartitionedWriteSuite extends QueryTest {
  import TestSQLContext.implicits._

  test("write many partitions") {
    val path = Utils.createTempDir()
    path.delete()

    val df = TestSQLContext.range(100).select($"id", lit(1).as("data"))
    df.write.partitionBy("id").save(path.getCanonicalPath)

    checkAnswer(
      TestSQLContext.read.load(path.getCanonicalPath),
      (0 to 99).map(Row(1, _)).toSeq)

    Utils.deleteRecursively(path)
  }

  test("write many partitions with repeats") {
    val path = Utils.createTempDir()
    path.delete()

    val base = TestSQLContext.range(100)
    val df = base.unionAll(base).select($"id", lit(1).as("data"))
    df.write.partitionBy("id").save(path.getCanonicalPath)

    checkAnswer(
      TestSQLContext.read.load(path.getCanonicalPath),
      (0 to 99).map(Row(1, _)).toSeq ++ (0 to 99).map(Row(1, _)).toSeq)

    Utils.deleteRecursively(path)
  }
}
