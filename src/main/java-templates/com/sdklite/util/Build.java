package com.sdklite.util;

/**
 * The build properties
 * 
 * @author johnsonlee
 *
 */
public final class Build {

    public static final String GROUP_ID = "${project.groupId}";

    public static final String ARTIFACT_ID = "${project.artifactId}";

    public static final String VERSION = "${project.version}";

    public static final String REVISION = "${buildNumber}";

}
