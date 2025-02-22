package com.hunter.web.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchSpecificationBuilder {

	private static List<SearchCriteria> listOfCriteria;
	static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public static Object build(String fromDate, String toDate, String keyword, Class<?> classObj) {

		try {
			listOfCriteria = new ArrayList<>();

			if(keyword != null && !keyword.equals("")) {
				for (Field field : classObj.getDeclaredFields()) {
					if(field.getGenericType().getTypeName().equals("java.lang.String")) {
						listOfCriteria.add(new SearchCriteria(field.getName(), ":", keyword, true));
					};
					//java.lang.Integer, java.lang.Double, int, long, double
				}
			}

			if(fromDate != null && !fromDate.equalsIgnoreCase("") && toDate != null && !toDate.equalsIgnoreCase("")) {
				listOfCriteria.add(new SearchCriteria("date", ">", formatter.parse(fromDate)));
				listOfCriteria.add(new SearchCriteria("date", "<", formatter.parse(toDate)));

			} else if((fromDate == null || fromDate.equalsIgnoreCase("")) && toDate != null && !toDate.equalsIgnoreCase("")) {
				listOfCriteria.add(new SearchCriteria("date", "<", formatter.parse(toDate)));

			} else if((toDate == null || toDate.equalsIgnoreCase("")) && fromDate != null && !fromDate.equalsIgnoreCase("")) {
				listOfCriteria.add(new SearchCriteria("date", ">", formatter.parse(fromDate)));

			}

			String entityPackageName = classObj.getPackageName();
			String specificationClassName = entityPackageName.substring(0, entityPackageName.lastIndexOf(".")) 
					+ ".specification." + classObj.getSimpleName() + "SearchSpecification";

			return Class.forName(specificationClassName).getConstructor(List.class).newInstance(listOfCriteria);

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | 
				InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | ParseException e) {
			e.printStackTrace();
		}

		return null;

	}
}
