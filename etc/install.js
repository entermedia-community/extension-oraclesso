importPackage( Packages.com.openedit.util );
importPackage( Packages.java.util );
importPackage( Packages.java.lang );
importPackage( Packages.com.openedit.modules.update );

var zip = "http://dev.entermediasoftware.com/jenkins/job/extension-oraclesso/lastSuccessfulBuild/artifact/deploy/extension-oraclesso.zip";

var root = moduleManager.getBean("root").getAbsolutePath();
var tmp = root + "/WEB-INF/tmp";

log.add("1. GET THE LATEST ZIP FILE");
var downloader = new Downloader();
downloader.download( zip, tmp + "/extension-oraclesso.zip");

log.add("2. UNZIP WAR FILE");
var unziper = new ZipUtil();
unziper.unzip(  tmp + "/extension-oraclesso.zip",  tmp );

var files = new FileUtils();
log.add("4. UPGRADE BASE DIR");
files.deleteAll( root + "/WEB-INF/base/system/components/authentication/oraclesso");
files.deleteAll( root + "/WEB-INF/base/system/components/authentication/nopermissions.xconf");
files.copyFiles( tmp + "/webapp/eml/authentication/oraclesso", root + "/WEB-INF/base/system/components/authentication");
files.copyFiles( tmp + "/webapp/webapp/eml/authentication/nopermissions.xconf", root + "/WEB-INF/base/system/components/authentication");

log.add("5. CLEAN UP");
files.deleteAll(tmp);

log.add("6. UPGRADE COMPLETED");