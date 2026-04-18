export enum HubAction {
  CREATE = "CREATE",
  READ = "READ",
  UPDATE = "UPDATE",
  DELETE = "DELETE",
}

export interface InviteHubRequest {
  targetEmail: string;
  permissions: HubAction[];
}

export interface InviteHubResponse {
  hubAlias: string;
  targetEmail: string;
  permissions: HubAction[];
}

export interface RegisterHubRequest {
  serialNumber: string;
  alias: string;
}

export interface RegisterHubResponse {
  serialNumber: string;
  alias: string;
  createdAt: string;
}

export interface AccessibleHub {
  id: number;
  serialNumber: string;
  alias: string;
  isOwner: boolean;
}

export interface GetHubsResponse {
  accessibleHubs: AccessibleHub[];
  totalCount: number;
  page: number;
  size: number;
  hasNext: boolean;
}

export interface DeleteHubResponse {
  serialNumber: string;
  deletedAt: string;
}

export interface ChangeHubAliasRequest {
  alias: string;
}

export interface ChangeHubAliasResponse {
  changedAlias: string;
  changedAt: string;
}

export interface SendHubCommandRequest {
  type: string;
  payload: Record<string, unknown>;
}

export interface SendHubCommandResponse {
  requestId: string;
  type: string;
}
