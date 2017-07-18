/* Load this script using conditional IE comments if you need to support IE 7 and IE 6. */

window.onload = function() {
	function addIcon(el, entity) {
		var html = el.innerHTML;
		el.innerHTML = '<span style="font-family: \'knowprocess\'">' + entity + '</span>' + html;
	}
	var icons = {
			'kp-icon-linkedin' : '&#xf0e1;',
			'kp-icon-sitemap' : '&#xf0e8;',
			'kp-icon-comments-alt' : '&#xf0e6;',
			'kp-icon-rss' : '&#xf09e;',
			'kp-icon-twitter-sign' : '&#xf081;',
			'kp-icon-cloud' : '&#xf0c2;',
			'kp-icon-road' : '&#xf018;',
			'kp-icon-twitter' : '&#xe000;',
			'kp-icon-road-2' : '&#xe001;',
			'kp-icon-linkedin-sign' : '&#xf08c;',
			'kp-icon-linkedin-2' : '&#xe002;',
			'kp-icon-feed' : '&#xe003;',
			'kp-icon-feed-2' : '&#xe004;',
			'kp-icon-inbox' : '&#xf01c;',
			'kp-icon-drawer' : '&#xe005;',
			'kp-icon-user' : '&#xf007;',
			'kp-icon-user-2' : '&#xe006;',
			'kp-icon-profile' : '&#xe007;',
			'kp-icon-cog' : '&#xf013;',
			'kp-icon-cogs' : '&#xf085;',
			'kp-icon-cog-2' : '&#xe008;',
			'kp-icon-cog-3' : '&#xe009;',
			'kp-icon-cogs-2' : '&#xe00a;',
			'kp-icon-upload' : '&#xf01b;',
			'kp-icon-upload-alt' : '&#xf093;',
			'kp-icon-upload-2' : '&#xe00b;',
			'kp-icon-cloud-upload' : '&#xf0ee;',
			'kp-icon-download-alt' : '&#xf019;',
			'kp-icon-download' : '&#xf01a;',
			'kp-icon-cloud-download' : '&#xf0ed;',
			'kp-icon-download-2' : '&#xe00c;',
			'kp-icon-cloud-download-2' : '&#xe00d;',
			'kp-icon-plus-sign' : '&#xf055;',
			'kp-icon-minus-sign' : '&#xf056;',
			'kp-icon-remove-sign' : '&#xf057;',
			'kp-icon-ok-sign' : '&#xf058;',
			'kp-icon-question-sign' : '&#xf059;',
			'kp-icon-info-sign' : '&#xf05a;',
			'kp-icon-exclamation-sign' : '&#xf06a;',
			'kp-icon-warning-sign' : '&#xf071;',
			'kp-icon-signout' : '&#xf08b;',
			'kp-icon-signin' : '&#xf090;',
			'kp-icon-enter' : '&#xe00e;',
			'kp-icon-exit' : '&#xe00f;',
			'kp-icon-warning' : '&#xe010;',
			'kp-icon-signup' : '&#xe011;',
			'kp-icon-search' : '&#xf002;',
			'kp-icon-search-2' : '&#xe012;',
			'kp-icon-facebook-sign' : '&#xf082;',
			'kp-icon-facebook' : '&#xf09a;',
			'kp-icon-facebook-2' : '&#xe013;',
			'kp-icon-facebook-3' : '&#xe014;',
			'kp-icon-happy' : '&#xe015;',
			'kp-icon-happy-2' : '&#xe016;',
			'kp-icon-smiley' : '&#xe017;',
			'kp-icon-smiley-2' : '&#xe018;',
			'kp-icon-tongue' : '&#xe019;',
			'kp-icon-tongue-2' : '&#xe01a;',
			'kp-icon-sad' : '&#xe01b;',
			'kp-icon-sad-2' : '&#xe01c;',
			'kp-icon-wink' : '&#xe01d;',
			'kp-icon-wink-2' : '&#xe01e;',
			'kp-icon-grin' : '&#xe01f;',
			'kp-icon-grin-2' : '&#xe020;',
			'kp-icon-cool' : '&#xe021;',
			'kp-icon-cool-2' : '&#xe022;',
			'kp-icon-angry' : '&#xe023;',
			'kp-icon-angry-2' : '&#xe024;',
			'kp-icon-evil' : '&#xe025;',
			'kp-icon-evil-2' : '&#xe026;',
			'kp-icon-shocked' : '&#xe027;',
			'kp-icon-shocked-2' : '&#xe028;',
			'kp-icon-confused' : '&#xe029;',
			'kp-icon-confused-2' : '&#xe02a;',
			'kp-icon-neutral' : '&#xe02b;',
			'kp-icon-neutral-2' : '&#xe02c;',
			'kp-icon-wondering' : '&#xe02d;',
			'kp-icon-wondering-2' : '&#xe02e;',
			'kp-icon-spinner' : '&#xf110;',
			'kp-icon-home' : '&#xf015;',
			'kp-icon-home-2' : '&#xe02f;',
			'kp-icon-home-3' : '&#xe030;',
			'kp-icon-tag' : '&#xf02b;',
			'kp-icon-tags' : '&#xf02c;',
			'kp-icon-list' : '&#xf03a;',
			'kp-icon-list-ul' : '&#xf0ca;',
			'kp-icon-menu' : '&#xe031;',
			'kp-icon-th-list' : '&#xf00b;',
			'kp-icon-wordpress' : '&#xe032;',
			'kp-icon-wordpress-2' : '&#xe033;',
			'kp-icon-google-plus-sign' : '&#xf0d4;',
			'kp-icon-google-plus' : '&#xf0d5;',
			'kp-icon-google' : '&#xe034;',
			'kp-icon-google-plus-2' : '&#xe035;',
			'kp-icon-google-plus-3' : '&#xe036;',
			'kp-icon-google-drive' : '&#xe037;',
			'kp-icon-github-sign' : '&#xf092;',
			'kp-icon-github-alt' : '&#xf113;',
			'kp-icon-github' : '&#xe038;',
			'kp-icon-github-2' : '&#xe039;',
			'kp-icon-github-3' : '&#xe03a;',
			'kp-icon-github-4' : '&#xe03b;',
			'kp-icon-github-5' : '&#xe03c;',
			'kp-icon-github-6' : '&#xf09b;',
			'kp-icon-expand' : '&#xe03d;',
			'kp-icon-fullscreen' : '&#xf0b2;',
			'kp-icon-contract' : '&#xe03e;',
			'kp-icon-thumbs-up' : '&#xf087;',
			'kp-icon-thumbs-down' : '&#xf088;',
			'kp-icon-bookmark-empty' : '&#xf097;',
			'kp-icon-check-empty' : '&#xf096;',
			'kp-icon-table' : '&#xf0ce;',
			'kp-icon-star' : '&#xe03f;',
			'kp-icon-star-2' : '&#xe040;',
			'kp-icon-star-3' : '&#xe041;',
			'kp-icon-thumbs-up-2' : '&#xe042;',
			'kp-icon-thumbs-up-3' : '&#xe043;',
			'kp-icon-share' : '&#xe044;',
			'kp-icon-twitter-2' : '&#xf099;',
			'kp-icon-group' : '&#xf0c0;',
			'kp-icon-bell' : '&#xf0a2;',
			'kp-icon-off' : '&#xf011;',
			'kp-icon-radio-checked' : '&#xe045;',
			'kp-icon-radio-unchecked' : '&#xe046;',
			'kp-icon-filter' : '&#xe047;',
			'kp-icon-filter-2' : '&#xe048;',
			'kp-icon-check' : '&#xf046;',
			'kp-icon-stats' : '&#xe049;',
			'kp-icon-bar-chart' : '&#xf080;',
			'kp-icon-pie' : '&#xe04a;'
		},
		els = document.getElementsByTagName('*'),
		i, attr, html, c, el;
	for (i = 0; ; i += 1) {
		el = els[i];
		if(!el) {
			break;
		}
		attr = el.getAttribute('data-icon');
		if (attr) {
			addIcon(el, attr);
		}
		c = el.className;
		c = c.match(/kp-icon-[^\s'"]+/);
		if (c && icons[c[0]]) {
			addIcon(el, icons[c[0]]);
		}
	}
};