<template>
  <div class="chart-wrapper">
    <canvas :ref="canvasRef"></canvas>
  </div>
</template>

<script>
import { Chart, registerables } from "chart.js";
import { defineComponent, onMounted, onBeforeUnmount, watch, ref } from "vue";

Chart.register(...registerables);

export default defineComponent({
  name: "LineChart",
  props: {
    chartData: {
      type: Object,
      required: true,
    },
    options: {
      type: Object,
      default: () => ({}),
    },
  },
  setup(props) {
    const chartInstance = ref(null);
    const canvas = ref(null);

    const canvasRef = (el) => {
      canvas.value = el;
    };

    const createChart = () => {
      if (chartInstance.value) {
        chartInstance.value.destroy();
      }

      if (canvas.value && props.chartData) {
        const ctx = canvas.value.getContext("2d");
        chartInstance.value = new Chart(ctx, {
          type: "line",
          data: {
            ...props.chartData,
            datasets: props.chartData.datasets.map((dataset) => ({
              ...dataset,
              borderColor: "#10b981",
              backgroundColor: "rgba(16, 185, 129, 0.1)",
              borderWidth: 2,
              pointBackgroundColor: "#10b981",
              pointBorderColor: "#ffffff",
              pointBorderWidth: 2,
              pointRadius: 4,
              pointHoverRadius: 6,
              fill: true,
              tension: 0.4,
            })),
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            layout: {
              padding: {
                top: 20,
                right: 20,
                bottom: 20,
                left: 20,
              },
            },
            plugins: {
              legend: {
                display: false,
              },
              tooltip: {
                backgroundColor: "rgba(15, 23, 42, 0.9)",
                titleColor: "#ffffff",
                bodyColor: "#ffffff",
                padding: 12,
                cornerRadius: 8,
                displayColors: false,
                callbacks: {
                  ...props.options?.plugins?.tooltip?.callbacks,
                },
              },
            },
            scales: {
              x: {
                grid: {
                  display: false,
                },
                ticks: {
                  color: "#64748b",
                  font: {
                    size: 12,
                  },
                },
              },
              y: {
                beginAtZero: true,
                grid: {
                  color: "rgba(226, 232, 240, 0.6)",
                },
                ticks: {
                  color: "#64748b",
                  font: {
                    size: 12,
                  },
                  padding: 8,
                  callback: props.options?.scales?.y?.ticks?.callback,
                },
              },
            },
            ...props.options,
          },
        });
      }
    };

    onMounted(() => {
      createChart();
    });

    onBeforeUnmount(() => {
      if (chartInstance.value) {
        chartInstance.value.destroy();
      }
    });

    watch(() => props.chartData, createChart, { deep: true });
    watch(() => props.options, createChart, { deep: true });

    return {
      canvasRef,
    };
  },
});
</script>

<style scoped>
.chart-wrapper {
  width: 100%;
  height: 100%;
  min-height: 300px;
  background: white;
  border-radius: 0.75rem;
  padding: 1rem;
}
</style>
