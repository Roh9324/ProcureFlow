export interface RequestHistoryItem {
  id: number;
  assetRequestId: number;
  oldStatus: string | null;
  newStatus: string;
  action: string;
  comment: string;
  changedByName: string;
  changedByEmail: string;
  changedAt: string;
}
