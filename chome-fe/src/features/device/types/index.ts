export interface AccessibleDevice {
  id: number;
  serialNumber: string;
  name: string;
  type: string;
  alias: string;
}

export interface GetDevicesResponse {
  devices: AccessibleDevice[];
}

export interface CommandInfo {
  id: number;
  command: string;
  description: string;
}

export interface DeviceDetail {
  id: number;
  serialNumber: string;
  name: string;
  type: string;
  alias: string;
  commands: CommandInfo[];
}

export interface ChangeDeviceAliasRequest {
  alias: string;
}

export interface ChangeDeviceAliasResponse {
  changedAlias: string;
  changedAt: string;
}
