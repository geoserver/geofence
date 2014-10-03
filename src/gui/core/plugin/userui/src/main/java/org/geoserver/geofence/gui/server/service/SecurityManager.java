/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.service;

//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.GrantedAuthorityImpl;

/**
 * The Class SecurityManager.
 */
public class SecurityManager
{

    // Logger LOGGER = LogManager.getLogger(SecurityManager.class.getName());
    //
    // private List<String> authorizedRoles;
    //
    // private MemberServiceInternal memberService;
    //
    // /**
    // * @param authorizedRoles the authorizedRoles to set
    // */
    // public void setAuthorizedRoles(List<String> authorizedRoles) {
    // this.authorizedRoles = authorizedRoles;
    // }
    //
    // /**
    // * @return the authorizedRoles
    // */
    // public List<String> getAuthorizedRoles() {
    // return authorizedRoles;
    // }
    //
    // /**
    // * @param memberService the memberService to set
    // */
    // public void setMemberService(MemberServiceInternal memberService) {
    // this.memberService = memberService;
    // }
    //
    // /**
    // * @return the memberService
    // */
    // public MemberServiceInternal getMemberService() {
    // return memberService;
    // }
    //
    // public Collection<GrantedAuthority> attemptAuthentication(String username, String password)
    // throws Exception {
    // /**
    // * Spring Security
    // */
    // /*AuthenticationManager am = (AuthenticationManager)
    // ctx.getBean("OpenSDIAuthenticationManager");
    //
    // try {
    // UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username,
    // password);
    // Authentication res = am.authenticate(auth);
    // return res.getAuthorities();
    // }catch(Exception e) {
    // log.warn("Authentication Failed: " + e.getLocalizedMessage());
    // return new GrantedAuthority[]{new GrantedAuthorityImpl("BAD_CREDENTIALS")};
    // } */
    //
    // /**
    // * DG Member Service
    // */
    // CredentialDto credentials = new CredentialDto();
    // credentials.setUsername(username);
    // credentials.setPassword(password);
    // // Do not set connect id - needs to be empty for Member Services
    // //credentials.setConnectId(username);
    //
    // final Collection<GrantedAuthority> memberRoles = new ArrayList<GrantedAuthority>();
    // try {
    // boolean authorized = false;
    //
    // MemberDto member = memberService.login(credentials);
    // for (Role role : member.getRoles()) {
    // LOGGER.info("Checking role " + role.getName().toUpperCase() + " for member " +
    // member.getUsername());
    // if (authorized = getAuthorizedRoles().contains(role.getName().toUpperCase())) {
    // break;
    // }
    // }
    //
    // if (authorized) {
    // LOGGER.info("Authorized member " + member.getUsername());
    // for (Role role : member.getRoles()) {
    // memberRoles.add(new GrantedAuthorityImpl(role.getName().toUpperCase()));
    // }
    //
    // return memberRoles;
    // } else {
    // LOGGER.info("Invalid role for member " + member.getUsername());
    // memberRoles.add(new GrantedAuthorityImpl("BAD CREDENTIALS"));
    // return memberRoles;
    // }
    // }
    // catch (Exception e) {
    // LOGGER.severe("invalid login- memberService: " + e);
    // LOGGER.warning("Authentication Failed: " + e.getLocalizedMessage());
    // memberRoles.add(new GrantedAuthorityImpl("BAD CREDENTIALS"));
    // return memberRoles;
    // }
    // }
}
