package rcn.web.util;

import lombok.Getter;

@Getter
public class SearchCriteria{
		private String key;
		private String operation;
		private Object value;
		private String joinTable;
		private boolean isOr = false;

		public SearchCriteria(String key, String operation, Object value) {
			this.key = key;
			this.operation = operation;
			this.value = value;
		}
		
		public SearchCriteria(String key, String operation, Object value, boolean isOr) {
			this.key = key;
			this.operation = operation;
			this.value = value;
			this.isOr = isOr;
		}
		
		public SearchCriteria(String key, String operation, Object value, String joinTable) {
			this.key = key;
			this.operation = operation;
			this.value = value;
			this.joinTable = joinTable;
			this.isOr = true;
		}

	}