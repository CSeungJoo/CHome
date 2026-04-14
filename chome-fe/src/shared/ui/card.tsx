import { ReactNode } from "react";

interface CardProps {
  children: ReactNode;
  className?: string;
}

export function Card({ children, className = "" }: CardProps) {
  return (
    <div
      className={`bg-gray-900 rounded-xl border border-gray-800 shadow-sm p-6 ${className}`}
    >
      {children}
    </div>
  );
}
