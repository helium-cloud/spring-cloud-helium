function CookieHelper() {
	this.getCookie = function(name) {
		return this.getCookie(name, false);
	};
	this.getCookie = function(name, ifNotExitCreate) {
		var arrStr = document.cookie.split("; ");
		for (var i = 0; i < arrStr.length; i++) {
			var temp = arrStr[i].split("=");
			if (temp[0] == name)
				return unescape(temp[1]);
		}
		if (ifNotExitCreate) {
			var value = this.randInt();
			this.setCookie(name, value);
			return value;
		} else {
			return null;
		}

	};
	this.setCookie = function(name, value) {
		var then = new Date();
		then.setTime(then.getTime() + 4 * 60 * 1000);
		document.cookie = name + "=" + value + ";expires=" + then.toGMTString();
	};
	this.randInt = function() {
		var number = 9999999999999999;
		var rnd = new Date().getTime();
		rnd = (rnd * 9301 + 49297) % 233280;
		rnd = rnd / (233280.0);
		return Math.ceil(rnd * number);
	};
}