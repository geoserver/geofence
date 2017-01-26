<?xml version="1.0" encoding="UTF-8"?>

<!--
   Transforms the output of the rules/ REST call into a more readable HTML table.

   There's no facility in GeoFence at the moment to apply this XSL, so use it
   using and external XSLT engine.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <xsl:template match="/RuleList">
        <html>
            <head>
                <title>GeoFence rulez</title>
                <style>
table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
}

td {
    vertical-align: top;
}

table td table   {
    border: 0px;
    width: 100%;
}

                </style>
            </head>
            <body>
                <table>
                    <tr>
                        <th>id</th>
                        <th>pri</th>
                        <th>user</th>
                        <th>role</th>
                        <th>instance</th>
                        <th>IP</th>
                        <th>service</th>
                        <th>req</th>
                        <th>workspace</th>
                        <th>layer</th>
                        <th>grant</th>
                        <th>constraints</th>
                    </tr>

                    <xsl:apply-templates select="rule"/>

                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="rule">
        <tr>
            <td><xsl:value-of select="id"/></td>
            <td><xsl:value-of select="priority"/></td>
            <td><xsl:value-of select="username"/></td>
            <td><xsl:value-of select="rolename"/></td>
            <td><xsl:value-of select="instance/name"/></td>
            <td><xsl:value-of select="ipaddress"/></td>
            <td><xsl:value-of select="service"/></td>
            <td><xsl:value-of select="request"/></td>
            <td><xsl:value-of select="workspace"/></td>
            <td><xsl:value-of select="layer"/></td>
            <td><xsl:value-of select="@grant"/></td>
            <td><xsl:apply-templates select="constraints"/></td>
        </tr>
    </xsl:template>

    <xsl:template match="constraints">
        <table>
            <xsl:apply-templates select="attributes"/>
        </table>
    </xsl:template>

    <xsl:template match="attribute">
        <tr>
            <td>ATTR: <xsl:value-of select="name"/></td>
            <td><xsl:value-of select="@access"/></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
