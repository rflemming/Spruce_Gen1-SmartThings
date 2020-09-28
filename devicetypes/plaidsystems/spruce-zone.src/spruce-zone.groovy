/**
 *  Spruce Controller zone child *
 *  Copyright 2020 Plaid Systems
 *
 *    Author: NC
 *    Date: 2020
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 -------------9-2020 update---------------
 * attempt Hubitat Compatibility
 -------------8-2020 update---------------
 * child for composite Spruce Controller
 * change commands to map to include additional duration value
 * "Dimmer" sets duration for Zone on - does not effect schedules
 */

 metadata {
    definition (name: "Spruce zone", namespace: "plaidsystems", author: "Plaid Systems", mnmn: "SmartThingsCommunity", vid: "generic-switch") {
        capability "Actuator"
        capability "Switch"
        capability "Switch Level"
        capability "Sensor"
        capability "Health Check"

        command "on"
        command "off"
        command "setLevel"
    }
    if (getIsST) {
        tiles {
            standardTile("switch", "device.switch", width: 2, height: 2) {
                state "off", label: "off", action: "on"
            state "on", label: "on", action: "off"
            }
            main "switch"
            details(["switch"])
        }
    }
}

def installed() {
    log.trace "Executing installed"
    getHubPlatform()
    initialize()
}

def updated() {
    log.trace "Executing updated"
    initialize()
}

private initialize() {
    log.trace "Executing initialize"

    sendEvent(name: "level", value: 5, descriptionText: "initialize level", displayed: false)
    sendEvent(name: "switch", value: "off", descriptionText: "initialize off", displayed: false)
    sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
    sendEvent(name: "healthStatus", value: "online")
    if (getIsSTHub) sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
    else            sendEvent(name: "DeviceWatch-Enroll", value: new groovy.json.JsonOutput().toJson([protocol: "cloud", scheme:"untracked"]), display: false, displayed: false)
}

def parse(String onoff) {
    log.debug "Child Desc: ${onoff}"
    sendEvent(name: "switch", value: onoff)
}

def on() {
    //log.debug "on ${device.latestValue("level")}"
    //parent.childOn(device.deviceNetworkId)
    def eventMap = createEvent(dni: device.deviceNetworkId, value: 'on', duration: device.latestValue("level").toInteger())
    parent.childOn(eventMap)
}

def off() {
    //log.debug "off ${device.latestValue("level")}"
    //parent.childOff(device.deviceNetworkId)
    def eventMap = createEvent(dni: device.deviceNetworkId, value: 'off', duration: 0)
    parent.childOff(eventMap)
}

def setLevel(level) {
    log.debug level
    sendEvent(name: "level", value: level)
}

def ping() {
    // Intentionally left blank as parent should handle this
}

// **************************************************************************************************************************
// SmartThings/Hubitat Portability Library (SHPL)
// Copyright (c) 2019, Barry A. Burke (storageanarchy@gmail.com)
//
// The following 3 calls are safe to use anywhere within a Device Handler or Application
//  - these can be called (e.g., if (getPlatform() == 'SmartThings'), or referenced (i.e., if (platform == 'Hubitat') )
//  - performance of the non-native platform is horrendous, so it is best to use these only in the metadata{} section of a
//    Device Handler or Application
//
private String  getPlatform() { (physicalgraph?.device?.HubAction ? 'SmartThings' : 'Hubitat') }    // if (platform == 'SmartThings') ...
private Boolean getIsST()     { (physicalgraph?.device?.HubAction ? true : false) }                 // if (isST) ...
private Boolean getIsHE()     { (hubitat?.device?.HubAction ? true : false) }                       // if (isHE) ...
//
// The following 3 calls are ONLY for use within the Device Handler or Application runtime
//  - they will throw an error at compile time if used within metadata, usually complaining that "state" is not defined
//  - getHubPlatform() ***MUST*** be called from the installed() method, then use "state.hubPlatform" elsewhere
//  - "if (state.isST)" is more efficient than "if (isSTHub)"
//
private String getHubPlatform() {
    if (state?.hubPlatform == null) {
        state.hubPlatform = getPlatform()                        // if (hubPlatform == 'Hubitat') ... or if (state.hubPlatform == 'SmartThings')...
        state.isST = state.hubPlatform.startsWith('S')           // if (state.isST) ...
        state.isHE = state.hubPlatform.startsWith('H')           // if (state.isHE) ...
    }
    return state.hubPlatform
}
private Boolean getIsSTHub() { (state.isST) }                    // if (isSTHub) ...
private Boolean getIsHEHub() { (state.isHE) }                    // if (isHEHub) ...
//
// **************************************************************************************************************************