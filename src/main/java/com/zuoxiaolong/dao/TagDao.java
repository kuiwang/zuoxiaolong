package com.zuoxiaolong.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuoxiaolong.util.StringUtil;

/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author 左潇龙
 * @since 2015年5月29日 上午1:04:31
 */
public abstract class TagDao extends BaseDao {
	
	public static List<Map<String, String>> getHotTags() {
		return execute(new Operation<List<Map<String, String>>>() {
			@Override
			public List<Map<String, String>> doInConnection(Connection connection) {
				List<Map<String, String>> result = new ArrayList<Map<String,String>>();
				try {
					Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery("select * from tags order by (select count(article_id) from article_tag where tag_id=id) desc");
					while (resultSet.next()) {
						result.add(transfer(resultSet));
					}
				} catch (SQLException e) {
					error("query article_category failed ..." , e);
				}
				return result;
			}
		});
	}
	
	public static List<Map<String, String>> getTags(final int articleId) {
		return execute(new Operation<List<Map<String, String>>>() {
			@Override
			public List<Map<String, String>> doInConnection(Connection connection) {
				List<Map<String, String>> result = new ArrayList<Map<String,String>>();
				try {
					PreparedStatement statement = connection.prepareStatement("select * from tags where id in (select tag_id from article_tag where article_id=?)");
					statement.setInt(1, articleId);
					ResultSet resultSet = statement.executeQuery();
					while (resultSet.next()) {
						result.add(transfer(resultSet));
					}
				} catch (SQLException e) {
					error("query article_category failed ..." , e);
				}
				return result;
			}
		});
	}

	public static Integer save(final String tagName) {
		return execute(new TransactionalOperation<Integer>() {
			@Override
			public Integer doInConnection(Connection connection) {
				try {
					PreparedStatement statement = connection.prepareStatement("insert into tags (tag_name) values (?)",Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, tagName);
					int result = statement.executeUpdate();
					if (result > 0) {
						ResultSet resultSet = statement.getGeneratedKeys();
						if (resultSet.next()) {
							return resultSet.getInt(1);
						}
					}
				} catch (SQLException e) {
					error("save tags failed ..." , e);
				}
				return null;
			}
		});
	} 
	
	public static Integer getId(final String tagName) {
		return execute(new Operation<Integer>() {
			@Override
			public Integer doInConnection(Connection connection) {
				try {
					PreparedStatement statement = connection.prepareStatement("select id from tags where tag_name=?");
					statement.setString(1, tagName);
					ResultSet resultSet = statement.executeQuery();
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
				} catch (SQLException e) {
					error("query tags failed ..." , e);
				}
				return null;
			}
		});
	}
	
	public static Map<String, String> transfer(ResultSet resultSet){
		Map<String, String> tag = new HashMap<String, String>();
		try {
			tag.put("id", resultSet.getString("id"));
			String tagName = resultSet.getString("tag_name");
			tag.put("tag_name", tagName);
			tag.put("short_tag_name", StringUtil.substring(tagName, 4));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return tag;
	}
	
}
