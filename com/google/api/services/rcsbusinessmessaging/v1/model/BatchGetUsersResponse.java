/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2021-04-19 13:59:11 EDT)
 * on 2021-04-19 at 17:59:33 UTC 
 * Modify at your own risk.
 */

package com.google.api.services.rcsbusinessmessaging.v1.model;

/**
 * Response message for RBMService.BatchGetUsers method.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the RCS Business Messaging API. For a detailed
 * explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class BatchGetUsersResponse extends com.google.api.client.json.GenericJson {

  /**
   * Amount of users' phone numbers from the randomly selected list that are RCS-enabled regardless
   * of the launch status for the agent. The ratio between this value and the
   * total_random_sample_user_count can be used to approximate the potential reach for a list of
   * users.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer reachableRandomSampleUserCount;

  /**
   * List of users' phone numbers that can be reached from the RBM platform. Only users on carriers
   * that the agent is launched on will be returned.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<java.lang.String> reachableUsers;

  /**
   * Amount of users' phone number randomly selected from the request. Typically this value will be
   * ~75% of the total requested users in the initial BatchGetUsers request. This value will be 0 if
   * the amount of requested users is less than 500.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer totalRandomSampleUserCount;

  /**
   * Amount of users' phone numbers from the randomly selected list that are RCS-enabled regardless
   * of the launch status for the agent. The ratio between this value and the
   * total_random_sample_user_count can be used to approximate the potential reach for a list of
   * users.
   * @return value or {@code null} for none
   */
  public java.lang.Integer getReachableRandomSampleUserCount() {
    return reachableRandomSampleUserCount;
  }

  /**
   * Amount of users' phone numbers from the randomly selected list that are RCS-enabled regardless
   * of the launch status for the agent. The ratio between this value and the
   * total_random_sample_user_count can be used to approximate the potential reach for a list of
   * users.
   * @param reachableRandomSampleUserCount reachableRandomSampleUserCount or {@code null} for none
   */
  public BatchGetUsersResponse setReachableRandomSampleUserCount(java.lang.Integer reachableRandomSampleUserCount) {
    this.reachableRandomSampleUserCount = reachableRandomSampleUserCount;
    return this;
  }

  /**
   * List of users' phone numbers that can be reached from the RBM platform. Only users on carriers
   * that the agent is launched on will be returned.
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getReachableUsers() {
    return reachableUsers;
  }

  /**
   * List of users' phone numbers that can be reached from the RBM platform. Only users on carriers
   * that the agent is launched on will be returned.
   * @param reachableUsers reachableUsers or {@code null} for none
   */
  public BatchGetUsersResponse setReachableUsers(java.util.List<java.lang.String> reachableUsers) {
    this.reachableUsers = reachableUsers;
    return this;
  }

  /**
   * Amount of users' phone number randomly selected from the request. Typically this value will be
   * ~75% of the total requested users in the initial BatchGetUsers request. This value will be 0 if
   * the amount of requested users is less than 500.
   * @return value or {@code null} for none
   */
  public java.lang.Integer getTotalRandomSampleUserCount() {
    return totalRandomSampleUserCount;
  }

  /**
   * Amount of users' phone number randomly selected from the request. Typically this value will be
   * ~75% of the total requested users in the initial BatchGetUsers request. This value will be 0 if
   * the amount of requested users is less than 500.
   * @param totalRandomSampleUserCount totalRandomSampleUserCount or {@code null} for none
   */
  public BatchGetUsersResponse setTotalRandomSampleUserCount(java.lang.Integer totalRandomSampleUserCount) {
    this.totalRandomSampleUserCount = totalRandomSampleUserCount;
    return this;
  }

  @Override
  public BatchGetUsersResponse set(String fieldName, Object value) {
    return (BatchGetUsersResponse) super.set(fieldName, value);
  }

  @Override
  public BatchGetUsersResponse clone() {
    return (BatchGetUsersResponse) super.clone();
  }

}