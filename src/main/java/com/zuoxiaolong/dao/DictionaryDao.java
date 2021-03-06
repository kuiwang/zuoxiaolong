package com.zuoxiaolong.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public abstract class DictionaryDao extends BaseDao {
	
	public static List<Map<String, String>> getDictionariesByType(final String type) {
		return execute(new Operation<List<Map<String, String>>>() {
			@Override
			public List<Map<String, String>> doInConnection(Connection connection) {
				List<Map<String, String>> result = new ArrayList<Map<String,String>>();
				try {
					PreparedStatement statement = connection.prepareStatement("select * from dictionaries where type=?");
					statement.setString(1, type);
					ResultSet resultSet = statement.executeQuery();
					while (resultSet.next()) {
						result.add(transfer(resultSet));
					}
				} catch (SQLException e) {
					error("query dictionaries failed ..." , e);
				}
				return result;
			}
		});
	}
	public static String getName(final Integer id) {
		return execute(new Operation<String>() {
			@Override
			public String doInConnection(Connection connection) {
				try {
					PreparedStatement statement = connection.prepareStatement("select name from dictionaries where id=?");
					statement.setInt(1, id);
					ResultSet resultSet = statement.executeQuery();
					if (resultSet.next()) {
						return resultSet.getString(1);
					}
				} catch (SQLException e) {
					error("query dictionaries failed ..." , e);
				}
				return null;
			}
		});
	}
	
	public static Map<String, String> transfer(ResultSet resultSet){
		Map<String, String> tag = new HashMap<String, String>();
		try {
			tag.put("id", resultSet.getString("id"));
			tag.put("name", resultSet.getString("name"));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return tag;
	}
	
}
