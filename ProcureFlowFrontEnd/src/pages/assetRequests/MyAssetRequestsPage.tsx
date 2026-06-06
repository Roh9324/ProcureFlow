import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMyAssetRequests } from '../../api/assetRequestApi';
import StatusBadge from '../../components/common/StatusBadge';
import type { AssetRequest } from '../../types/assetRequest';
import { formatDate } from '../../utils/dateUtils';
import { getApiErrorMessage } from '../../utils/errorUtils';

function MyAssetRequestsPage() {
  const [requests, setRequests] = useState<AssetRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadRequests = async () => {
      setLoading(true);
      setError('');

      try {
        const data = await getMyAssetRequests();
        setRequests(data);
      } catch (apiError) {
        setError(getApiErrorMessage(apiError, 'Unable to load asset requests.'));
      } finally {
        setLoading(false);
      }
    };

    loadRequests();
  }, []);

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Asset Requests</p>
          <h1>My Requests</h1>
          <p>Track the status of all asset requests you have submitted.</p>
        </div>
      </div>

      {error && <div className="alert error">{error}</div>}

      <div className="table-card">
        {loading ? (
          <div className="loading-box">Loading requests...</div>
        ) : requests.length === 0 ? (
          <div className="empty-state">No asset requests found.</div>
        ) : (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Asset Name</th>
                  <th>Quantity</th>
                  <th>Priority</th>
                  <th>Needed By</th>
                  <th>Status</th>
                  <th>Created At</th>
                  <th>Timeline</th>
                </tr>
              </thead>
              <tbody>
                {requests.map((request) => (
                  <tr key={request.id}>
                    <td>{request.id}</td>
                    <td>{request.assetName}</td>
                    <td>{request.quantity}</td>
                    <td>{request.priority}</td>
                    <td>{formatDate(request.neededByDate)}</td>
                    <td>
                      <StatusBadge status={request.status} />
                    </td>
                    <td>{formatDate(request.createdAt)}</td>
                    <td>
                      <Link
                        className="small-button link-button"
                        to={`/employee/asset-requests/${request.id}/history`}
                      >
                        View Timeline
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </section>
  );
}

export default MyAssetRequestsPage;
