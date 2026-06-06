import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getAssetRequestHistory } from '../../api/assetRequestApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import RequestTimeline from '../../components/common/RequestTimeline';
import type { RequestHistoryItem } from '../../types/requestHistory';
import { getApiErrorMessage } from '../../utils/errorUtils';

function HrRequestHistoryPage() {
  const { id } = useParams();
  const requestId = Number(id);
  const [history, setHistory] = useState<RequestHistoryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadHistory = async () => {
      if (!requestId) {
        setError('Invalid request ID.');
        setLoading(false);
        return;
      }

      setLoading(true);
      setError('');

      try {
        setHistory(await getAssetRequestHistory(requestId));
      } catch (apiError) {
        setError(getApiErrorMessage(apiError, 'Unable to load request timeline.'));
      } finally {
        setLoading(false);
      }
    };

    loadHistory();
  }, [requestId]);

  return (
    <section className="page-section">
      <div className="page-heading">
        <div>
          <p className="eyebrow">Request Timeline</p>
          <h1>Request #{requestId || '-'}</h1>
          <p>Review every workflow action and status transition.</p>
        </div>
        <Link className="secondary-button" to="/hr/requests">
          Back to Requests
        </Link>
      </div>

      {error && <div className="alert error">{error}</div>}
      <div className="table-card">
        {loading ? <LoadingSpinner label="Loading timeline..." /> : <RequestTimeline items={history} />}
      </div>
    </section>
  );
}

export default HrRequestHistoryPage;
