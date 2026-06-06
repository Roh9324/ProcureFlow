interface LoadingSpinnerProps {
  label?: string;
}

function LoadingSpinner({ label = 'Loading...' }: LoadingSpinnerProps) {
  return <div className="loading-box">{label}</div>;
}

export default LoadingSpinner;
