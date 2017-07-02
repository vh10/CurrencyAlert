import urllib

c = ["USD", "EUR", "AUD", "CAD", "CHF", "CNH", "CZK", "DKK", "GBP", "HKD",
            "HUF", "INR", "JPY", "MXN", "NOK", "NZD", "PLN", "RON", "RUB", "SEK", "TRY", "ZAR"];

r = ["ALL","AFN","ARS","AWG","AUD","AZN","BSD","BBD","BYR","BZD","BMD","BOB","BAM","BWP","BGN","BRL","BND","KHR","CAD","KYD","CLP","CNH","COP","CRC","HRK","CUP","CZK","DKK","DOP","EGP","SVC","EEK","EUR","FKP","FJD","GEL","GHC","GIP","GTQ","GGP","GYD","HNL","HKD","HUF","ISK","INR","IDR","IRR","IMP","ILS","JMD","JPY","JEP","KZT","KPW","KRW","KGS","LAK","LVL","LBP","LRD","LTL","MKD","MYR","MUR","MXN","MNT","MZN","NAD","NPR","ANG","NZD","NIO","NGN","NOK","OMR","PKR","PAB","PYG","PEN","PHP","PLN","QAR","RON","RUB","SHP","SAR","RSD","SCR","SGD","SBD","SOS","ZAR","LKR","SEK","CHF","SRD","SYP","TWD","THB","TTD","TRY","TVD","UAH","GBP","USD","UYU","UZS","VEF","VND","YER","ZWD"]
l = ["http://www.currencysymbols.in/flags/albania.png","http://www.currencysymbols.in/flags/afghanistan.png","http://www.currencysymbols.in/flags/argentina.png","http://www.currencysymbols.in/flags/aruba.png","http://www.currencysymbols.in/flags/australia.png","http://www.currencysymbols.in/flags/azerbaijan.png","http://www.currencysymbols.in/flags/bahamas.png","http://www.currencysymbols.in/flags/barbados.png","http://www.currencysymbols.in/flags/belarus.png","http://www.currencysymbols.in/flags/belize.png","http://www.currencysymbols.in/flags/bermuda.png","http://www.currencysymbols.in/flags/bolivia.png","http://www.currencysymbols.in/flags/Bosnia_and_Herzegovina.png","http://www.currencysymbols.in/flags/botswana.png","http://www.currencysymbols.in/flags/bulgaria.png","http://www.currencysymbols.in/flags/brazil.png","http://www.currencysymbols.in/flags/brunei.png","http://www.currencysymbols.in/flags/cambodia.png","http://www.currencysymbols.in/flags/canada.png","http://www.currencysymbols.in/flags/Cayman_Islands.png","http://www.currencysymbols.in/flags/chile.png","http://www.currencysymbols.in/flags/china.png","http://www.currencysymbols.in/flags/colombia.png","http://www.currencysymbols.in/flags/costarica.png","http://www.currencysymbols.in/flags/croatia.png","http://www.currencysymbols.in/flags/cuba.png","http://www.currencysymbols.in/flags/czechrepublic.png","http://www.currencysymbols.in/flags/denmark.png","http://www.currencysymbols.in/flags/dominicanrepublic.png","http://www.currencysymbols.in/flags/egypt.png","http://www.currencysymbols.in/flags/elsalvador.png","http://www.currencysymbols.in/flags/estonia.png","http://www.currencysymbols.in/flags/euro.png","http://www.currencysymbols.in/flags/falklandislands.png","http://www.currencysymbols.in/flags/fiji.png","http://www.currencysymbols.in/flags/georgia.png","http://www.currencysymbols.in/flags/ghana.png","http://www.currencysymbols.in/flags/gibraltar.png","http://www.currencysymbols.in/flags/guatemala.png","http://www.currencysymbols.in/flags/guernsey.png","http://www.currencysymbols.in/flags/guyana.png","http://www.currencysymbols.in/flags/honduras.png","http://www.currencysymbols.in/flags/hongkong.png","http://www.currencysymbols.in/flags/hungary.png","http://www.currencysymbols.in/flags/iceland.png","http://www.currencysymbols.in/flags/india.png","http://www.currencysymbols.in/flags/indonesia.png","http://www.currencysymbols.in/flags/iran.png","http://www.currencysymbols.in/flags/isleofman.png","http://www.currencysymbols.in/flags/israel.png","http://www.currencysymbols.in/flags/jamaica.png","http://www.currencysymbols.in/flags/japan.png","http://www.currencysymbols.in/flags/jersey.png","http://www.currencysymbols.in/flags/kazakhstan.png","http://www.currencysymbols.in/flags/northkorea.png","http://www.currencysymbols.in/flags/southkorea.png","http://www.currencysymbols.in/flags/kyrgyzstan.png","http://www.currencysymbols.in/flags/laos.png","http://www.currencysymbols.in/flags/latvia.png","http://www.currencysymbols.in/flags/lebanon.png","http://www.currencysymbols.in/flags/liberia.png","http://www.currencysymbols.in/flags/lithuania.png","http://www.currencysymbols.in/flags/macedonia.png","http://www.currencysymbols.in/flags/malaysia.png","http://www.currencysymbols.in/flags/mauritius.png","http://www.currencysymbols.in/flags/mexico.png","http://www.currencysymbols.in/flags/mongolia.png","http://www.currencysymbols.in/flags/mozambique.png","http://www.currencysymbols.in/flags/namibia.png","http://www.currencysymbols.in/flags/nepal.png","http://www.currencysymbols.in/flags/netherlands.png","http://www.currencysymbols.in/flags/newzealand.png","http://www.currencysymbols.in/flags/nicaragua.png","http://www.currencysymbols.in/flags/nigeria.png","http://www.currencysymbols.in/flags/norway.png","http://www.currencysymbols.in/flags/oman.png","http://www.currencysymbols.in/flags/pakistan.png","http://www.currencysymbols.in/flags/panama.png","http://www.currencysymbols.in/flags/paraguay.png","http://www.currencysymbols.in/flags/peru.png","http://www.currencysymbols.in/flags/philippines.png","http://www.currencysymbols.in/flags/poland.png","http://www.currencysymbols.in/flags/qatar.png","http://www.currencysymbols.in/flags/romania.png","http://www.currencysymbols.in/flags/russia.png","http://www.currencysymbols.in/flags/sainthelena.png","http://www.currencysymbols.in/flags/saudiarabia.png","http://www.currencysymbols.in/flags/serbia.png","http://www.currencysymbols.in/flags/seychelles.png","http://www.currencysymbols.in/flags/singapore.png","http://www.currencysymbols.in/flags/solomonislands.png","http://www.currencysymbols.in/flags/somalia.png","http://www.currencysymbols.in/flags/southafrica.png","http://www.currencysymbols.in/flags/srilanka.png","http://www.currencysymbols.in/flags/sweden.png","http://www.currencysymbols.in/flags/switzerland.png","http://www.currencysymbols.in/flags/suriname.png","http://www.currencysymbols.in/flags/syria.png","http://www.currencysymbols.in/flags/taiwan.png","http://www.currencysymbols.in/flags/thailand.png","http://www.currencysymbols.in/flags/trinidadandtobago.png","http://www.currencysymbols.in/flags/turkey.png","http://www.currencysymbols.in/flags/tuvalu.png","http://www.currencysymbols.in/flags/ukraine.png","http://www.currencysymbols.in/flags/unitedkingdom.png","http://www.currencysymbols.in/flags/unitedstates.png","http://www.currencysymbols.in/flags/uruguay.png","http://www.currencysymbols.in/flags/uzbekistan.png","http://www.currencysymbols.in/flags/venezuela.png","http://www.currencysymbols.in/flags/vietnam.png","http://www.currencysymbols.in/flags/yemen.png","http://www.currencysymbols.in/flags/zimbabwe.png"];
for i in range(len(r)):
	if r[i] in c:
		testfile = urllib.URLopener()
		testfile.retrieve(l[i], "flag_" + r[i].lower() + ".png")
		print i